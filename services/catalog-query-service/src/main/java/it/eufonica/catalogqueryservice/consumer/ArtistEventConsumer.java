package it.eufonica.catalogqueryservice.consumer;

import it.eufonica.catalogqueryservice.event.artist.ArtistCreatedEvent;
import it.eufonica.catalogqueryservice.event.artist.ArtistUpdatedEvent;
import it.eufonica.catalogqueryservice.mapper.ArtistMapper;
import it.eufonica.catalogqueryservice.model.ArtistRead;
import it.eufonica.catalogqueryservice.processing.EventProcessingService;
import it.eufonica.catalogqueryservice.repository.ArtistRepository;
import it.eufonica.catalogqueryservice.versionchecker.VersionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component @Transactional
@RequiredArgsConstructor @Slf4j
public class ArtistEventConsumer {
    // Utility & Mappers
    private final VersionChecker versionChecker;
    private final ObjectMapper objectMapper;
    private final ArtistMapper artistMapper;
    private final CacheManager cacheManager;

    // Services
    private final EventProcessingService processingService;

    // Repositories
    private final ArtistRepository artistRepository;

    /**
     * Removes an artist from the "artists" cache.
     * <p>
     * We do this manually instead of using @CacheEvict on each handler method,
     * because the handlers are called from consume() in the SAME class
     * (self-invocation). Spring's caching annotations only work when a method
     * is called from OUTSIDE the class, through Spring's proxy. Calling a
     * method directly on "this" skips that proxy, so @CacheEvict would
     * silently do nothing here.
     *
     * @param artistId the id of the artist to remove from the cache
     */
    private void evictArtistCache(UUID artistId) {
        var cache = cacheManager.getCache("artists");
        if (cache != null) cache.evict(artistId);
    }

    @KafkaListener(topics = "${ARTIST_TOPIC_NAME:artist.events}")
    public void consume(@Payload String payload,
                        @Header("eventId") String eventIdHeader, @Header("eventType") String eventType) {
        log.debug("Received event for artist. eventId={}, eventType={}, payload={}", eventIdHeader, eventType, payload);
        UUID eventId = UUID.fromString(eventIdHeader);

        // Perform the correct action depending on the type of event
        switch (eventType) {
            case "ArtistCreatedEvent" -> handleArtistCreation(eventId, objectMapper.readValue(payload, ArtistCreatedEvent.class));
            case "ArtistUpdatedEvent" -> handleArtistUpdating(eventId, objectMapper.readValue(payload, ArtistUpdatedEvent.class));
            default -> log.warn("Unknown event type received. eventType={}", eventType);
        }
    }

    /**
     * Handles the creation of a new artist after an ArtistCreatedEvent is received.
     * <p>If the artist already exists and the event's version is higher
     * than the artist's current version, perform an upsertion.</p>
     * <p>If the version is lower than or equal to the artist's current version, ignore it.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleArtistCreation(UUID eventId, ArtistCreatedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "ArtistCreatedEvent", event.getId().toString())) return;

        ArtistRead existingArtist = artistRepository.findById(event.getId()).orElse(null);

        if (existingArtist != null) {
            // Case that artist already exists: upsertion

            // Check if the version of the event is lower than or equal to the current. If it is, ignore the event
            if (versionChecker.isStateRepresentationEventOutdated(event.getVersion(), existingArtist.getVersion()))
                return;

            log.debug("Upserting existing artist for event {}.", eventId);
            artistMapper.updateEntityFromCreationEvent(event, existingArtist);

            artistRepository.save(existingArtist);
        } else {
            // Case that artist doesn't exist: save as new

            log.debug("Creating artist for event {}.", eventId);

            artistRepository.save(artistMapper.toEntity(event));
        }

        evictArtistCache(event.getId());
    }

    /**
     * Handles the updating of a new artist after an ArtistUpdatedEvent is received.
     * <p>If event's version is lower than or equal to the artist's current
     * version, ignore the event.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleArtistUpdating(UUID eventId, ArtistUpdatedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "ArtistUpdatedEvent", event.getId().toString())) return;

        ArtistRead existingArtist = artistRepository.findById(event.getId()).orElse(null);

        if (existingArtist == null)
            log.warn("Trying to update an artist that doesn't exist in the projection. Creating it instead. " +
                    "eventId={}, artistId={}, artistName={}", eventId, event.getId(), event.getName());
        else
            // Check if the version of the event is lower than or equal to the current. If it is, ignore the event
            if (versionChecker.isStateRepresentationEventOutdated(event.getVersion(), existingArtist.getVersion()))
                return;

        artistRepository.save(artistMapper.toEntity(event));

        evictArtistCache(event.getId());
    }
}
