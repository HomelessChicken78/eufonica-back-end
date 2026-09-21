package it.eufonica.catalogqueryservice.consumer;

import it.eufonica.catalogqueryservice.event.album.*;
import it.eufonica.catalogqueryservice.mapper.AlbumMapper;
import it.eufonica.catalogqueryservice.model.AlbumContainsRead;
import it.eufonica.catalogqueryservice.model.AlbumRead;
import it.eufonica.catalogqueryservice.model.ArtAlbumRead;
import it.eufonica.catalogqueryservice.model.ArtistRead;
import it.eufonica.catalogqueryservice.processing.EventProcessingService;
import it.eufonica.catalogqueryservice.repository.AlbumContainsRepository;
import it.eufonica.catalogqueryservice.repository.AlbumRepository;
import it.eufonica.catalogqueryservice.repository.ArtAlbumRepository;
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
public class AlbumEventConsumer {
    // Utility & Mappers
    private final VersionChecker versionChecker;
    private final ObjectMapper objectMapper;
    private final AlbumMapper albumMapper;

    // Services
    private final EventProcessingService processingService;

    // Repositories
    private final AlbumRepository albumRepository;
    private final ArtAlbumRepository artAlbumRepository;
    private final AlbumContainsRepository albumContainsRepository;

    @KafkaListener(topics = "${ALBUM_TOPIC_NAME:album.events}")
    public void consume(@Payload String payload,
                        @Header("eventId") String eventIdHeader, @Header("eventType") String eventType) {
        log.debug("Received event for album. eventId={}, eventType={}, payload={}", eventIdHeader, eventType, payload);
        UUID eventId = UUID.fromString(eventIdHeader);

        switch (eventType) {
            case "AlbumCreatedEvent" -> handleAlbumCreation(eventId, objectMapper.readValue(payload, AlbumCreatedEvent.class));
            case "AlbumSongAddedEvent" -> handleAlbumSongAdded(eventId, objectMapper.readValue(payload, AlbumSongAddedEvent.class));
            case "AlbumSongRemovedEvent" -> handleAlbumSongRemoved(eventId, objectMapper.readValue(payload, AlbumSongRemovedEvent.class));
            case "AlbumUpdatedEvent" -> handleAlbumUpdatedEvent(eventId, objectMapper.readValue(payload, AlbumUpdatedEvent.class));
            case "DeletedAlbumEvent" -> handleAlbumDeletion(eventId, objectMapper.readValue(payload, DeletedAlbumEvent.class));
            default -> log.warn("Unknown event type received. eventType={}", eventType);
        }
    }

    /**
     * Handles the creation of a new album after a AlbumCreatedEvent is received.
     * <p>If the album already exists and the event's version is higher
     * than the album's current version, perform an upsertion.</p>
     * <p>If the version is lower than or equal to the album's current version, ignore it.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleAlbumCreation(UUID eventId, AlbumCreatedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "AlbumCreatedEvent", event.getId().toString())) return;

        AlbumRead existingAlbum = albumRepository.findById(event.getId()).orElse(null);

        if (existingAlbum != null) {
            // Case that artist already exists: upsertion

            // Check if the version of the event is lower than or equal to the current. If it is, ignore the event
            if (versionChecker.isStateRepresentationEventOutdated(event.getVersion(), existingAlbum.getVersion()))
                return;

            log.debug("Upserting existing album for event {}.", eventId);
            albumMapper.updateEntityFromCreationEvent(event, existingAlbum);

            albumRepository.save(existingAlbum);
        } else {
            // Case that artist doesn't exist: save as new

            log.debug("Creating album for event {}.", eventId);

            albumRepository.save(albumMapper.toEntity(event));
        }

        // Remove all the links between the album and the artists to guarantee a clean new state
        artAlbumRepository.deleteByAlbumId(event.getId());

        // Add all the links between the album and the artists back
        for (AlbumCreatedEvent.Artist art : event.getArtists())
            artAlbumRepository.save(new ArtAlbumRead(null, event.getId(), art.getId()));

        // Remove all the links between the album and the songs to guarantee a clean new state
        albumContainsRepository.deleteByAlbumId(event.getId());

        // Add all the links between the album and the songs back
        for (AlbumCreatedEvent.Song song : event.getSongs())
            albumContainsRepository.save(new AlbumContainsRead(null, event.getId(), song.getId()));
    }

    /**
     * Handles the attaching of a song to an album.
     * Compares the current album's version against the event's version.
     * <p>If the event's version is less than the album's current version,
     * assumes a mistakes is done and leaves the responsibility to manage
     * that event to the rest of the flow.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleAlbumSongAdded(UUID eventId, AlbumSongAddedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "AlbumSongAddedEvent", event.getSongId().toString())) return;

        // Look up the album this song applies to. If found, just log a warning when the event's
        // version looks anomalous (older than what's already stored) — this doesn't block processing,
        albumRepository.findById(event.getAlbumId()).ifPresent(album ->
                versionChecker.isDeltaVersionAnomalous(event.getVersion(), album.getVersion()));

        // Avoid inserting a duplicate link if the song is already associated with this album
        if (!albumContainsRepository.existsByAlbumIdAndSongId(event.getAlbumId(), event.getSongId()))
            albumContainsRepository.save(new AlbumContainsRead(null, event.getAlbumId(), event.getSongId()));
    }

    /**
     * Handles the detaching of a song to an album.
     * Compares the current album's version against the event's version.
     * <p>If the event's version is less than the album's current version,
     * assumes a mistakes is done and leaves the responsibility to manage
     * that event to the rest of the flow.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleAlbumSongRemoved(UUID eventId, AlbumSongRemovedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "AlbumSongRemovedEvent", event.getSongId().toString())) return;

        // Look up the album this song applies to. If found, just log a warning when the event's
        // version looks anomalous (older than what's already stored) — this doesn't block processing,
        albumRepository.findById(event.getAlbumId()).ifPresent(album ->
                versionChecker.isDeltaVersionAnomalous(event.getVersion(), album.getVersion()));

        albumContainsRepository.deleteByAlbumIdAndSongId(event.getAlbumId(), event.getSongId());
    }

    /**
     * Handles the updating of a new album after an AlbumUpdatedEvent is received.
     * <p>If event's version is lower than or equal to the album's current
     * version, ignore the event.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleAlbumUpdatedEvent(UUID eventId, AlbumUpdatedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "AlbumUpdatedEvent", event.getId().toString())) return;

        AlbumRead existingAlbum = albumRepository.findById(event.getId()).orElse(null);

        if (existingAlbum == null)
            log.warn("Trying to update an album that doesn't exist in the projection. Creating it instead. " +
                    "eventId={}, albumId={}, albumName={}", eventId, event.getId(), event.getName());
        else
            // Check if the version of the event is lower than or equal to the current. If it is, ignore the event
            if (versionChecker.isStateRepresentationEventOutdated(event.getVersion(), existingAlbum.getVersion()))
                return;

        albumRepository.save(albumMapper.toEntity(event));

        // Remove all the links between the album and the artists to guarantee a clean new state
        artAlbumRepository.deleteByAlbumId(event.getId());

        // Add all the links between the album and the artists back
        for (AlbumUpdatedEvent.Artist art : event.getArtists())
            artAlbumRepository.save(new ArtAlbumRead(null, event.getId(), art.getId()));

        // Remove all the links between the album and the songs to guarantee a clean new state
        albumContainsRepository.deleteByAlbumId(event.getId());

        // Add all the links between the album and the songs back
        for (AlbumUpdatedEvent.Song song : event.getSongs())
            albumContainsRepository.save(new AlbumContainsRead(null, event.getId(), song.getId()));
    }

    /**
     * Handles the removal of an album after a DeletedAlbumEvent is received.
     * <p>If the album doesn't exist, this does nothing aside from a
     * debug log, since deletion is idempotent by nature.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleAlbumDeletion(UUID eventId, DeletedAlbumEvent event) {
        if (!processingService.saveOrIgnore(eventId, "DeletedAlbumEvent", event.getId().toString())) return;

        // Delete the many-to-many link tables, since there is no
        // cascade configured - orphaned rows would otherwise be left
        // pointing at a non-existent album.
        artAlbumRepository.deleteByAlbumId(event.getId());
        albumContainsRepository.deleteByAlbumId(event.getId());

        albumRepository.deleteById(event.getId());

        if (albumRepository.existsById(event.getId()))
            log.debug("Deleted album for event {}. albumId={}", eventId, event.getId());
        else
            log.debug("Album already absent from projection, nothing to delete. eventId={}, albumId={}", eventId, event.getId());
    }
}
