package it.eufonica.catalogqueryservice.consumer;

import it.eufonica.catalogqueryservice.event.song.CreditedArtistAddedEvent;
import it.eufonica.catalogqueryservice.event.song.CreditedArtistRemovedEvent;
import it.eufonica.catalogqueryservice.event.song.SongCreatedEvent;
import it.eufonica.catalogqueryservice.mapper.SongMapper;
import it.eufonica.catalogqueryservice.model.SongCreditRead;
import it.eufonica.catalogqueryservice.model.SongRead;
import it.eufonica.catalogqueryservice.processing.EventProcessingService;
import it.eufonica.catalogqueryservice.repository.SongCreditRepository;
import it.eufonica.catalogqueryservice.repository.SongRepository;
import it.eufonica.catalogqueryservice.versionchecker.VersionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component @Transactional
@RequiredArgsConstructor @Slf4j
public class SongEventConsumer {
    // Utility & Mappers
    private final VersionChecker versionChecker;
    private final ObjectMapper objectMapper;
    private final SongMapper songMapper;

    // Services
    private final EventProcessingService processingService;

    // Repositories
    private final SongRepository songRepository;
    private final SongCreditRepository songCreditRepository;

    @KafkaListener(topics = "${SONG_TOPIC_NAME:song.events}")
    public void consume(@Payload String payload,
                        @Header("eventId") String eventIdHeader, @Header("eventType") String eventType) {
        log.debug("Received event for song. eventId={}, eventType={}, payload={}", eventIdHeader, eventType, payload);
        UUID eventId = UUID.fromString(eventIdHeader);

        // Perform the correct action depending on the type of event
        switch (eventType) {
            case "SongCreatedEvent" -> handleSongCreation(eventId, objectMapper.readValue(payload, SongCreatedEvent.class));
            case "CreditedArtistAddedEvent" -> handleCreditedArtistAdded(eventId, objectMapper.readValue(payload, CreditedArtistAddedEvent.class));
            case "CreditedArtistRemovedEvent" -> handleCreditedArtistRemoved(eventId, objectMapper.readValue(payload, CreditedArtistRemovedEvent.class));
            default -> log.warn("Unknown event type received. eventType={}", eventType);
        }
    }

    /**
     * Handles the creation of a new song after a SongCreatedEvent is received.
     * <p>If the song already exists and the event's version is higher
     * than the song's current version, perform an upsertion.</p>
     * <p>If the version is lower than or equal to the song's current version, ignore it.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleSongCreation(UUID eventId, SongCreatedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "SongCreatedEvent", event.getId().toString())) return;

        SongRead existingSong = songRepository.findById(event.getId()).orElse(null);

        if (existingSong != null) {
            // Case that song already exists: upsertion

            // Check if the version of the event is lower than or equal to the current. If it is, ignore the event
            if (versionChecker.isStateRepresentationEventOutdated(event.getVersion(), existingSong.getVersion()))
                return;

            log.debug("Upserting existing song for event {}.", eventId);
            songMapper.updateEntityFromCreationEvent(event, existingSong);

            songRepository.save(existingSong);
        } else  {
            // Case that song doesn't exist: save as new

            log.debug("Creating song for event {}.", eventId);

            songRepository.save(songMapper.toEntity(event));
        }

        // Remove all the links between the song and the credited artists to guarantee a clean new state
        songCreditRepository.deleteBySongId(event.getId());

        // Add all the links between the song and the credited artists back
        for (SongCreatedEvent.ArtistCredited credited : event.getCreditedArtists())
            songCreditRepository.save(new SongCreditRead(null, event.getId(), credited.getId()));
    }

    /**
     * Handles the attaching of a credited artist to a song.
     * Compares the current song's version against the event's version.
     * <p>If the event's version is less than the song's current version,
     * assumes a mistakes is done and leaves the responsibility to manage
     * that event to the rest of the flow.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleCreditedArtistAdded(UUID eventId, CreditedArtistAddedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "CreditedArtistAddedEvent", event.getSongId().toString())) return;

        // Look up the song this credit applies to. If found, just log a warning when the event's
        // version looks anomalous (older than what's already stored) — this doesn't block processing,
        songRepository.findById(event.getSongId())
                .ifPresent(song ->
                        versionChecker.isDeltaVersionAnomalous(event.getVersion(), song.getVersion())
                );

        songCreditRepository.save(new SongCreditRead(null, event.getSongId(), event.getCreditedArtistId()));
    }

    /**
     * Handles the detaching of a credited artist to a song.
     * Compares the current song's version against the event's version.
     * <p>If the event's version is less than the song's current version,
     * assumes a mistakes is done and leaves the responsibility to manage
     * that event to the rest of the flow.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleCreditedArtistRemoved(UUID eventId, CreditedArtistRemovedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "CreditedArtistRemovedEvent", event.getSongId().toString())) return;

        songRepository.findById(event.getSongId())
                .ifPresent(songRead ->
                        versionChecker.isDeltaVersionAnomalous(event.getVersion(), songRead.getVersion())
                );

        songCreditRepository.deleteBySongIdAndArtistId(event.getSongId(), event.getCreditedArtistId());
    }
}
