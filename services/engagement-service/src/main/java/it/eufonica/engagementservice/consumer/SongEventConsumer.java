package it.eufonica.engagementservice.consumer;

import it.eufonica.event.song.SongCreatedEvent;
import it.eufonica.event.song.SongDeletedEvent;
import it.eufonica.engagementservice.mapper.SongMapper;
import it.eufonica.engagementservice.model.Song;
import it.eufonica.event.processing.EventProcessingService;
import it.eufonica.engagementservice.repository.SongRepository;
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
    private final ObjectMapper objectMapper;
    private final SongMapper songMapper;

    // Services
    private final EventProcessingService processingService;

    // Repositories
    private final SongRepository songRepository;

    @KafkaListener(topics = "${SONG_TOPIC_NAME:song.events}")
    public void consume(@Payload String payload,
                        @Header("eventId") String eventIdHeader, @Header("eventType") String eventType) {
        log.debug("Received event for song. eventId={}, eventType={}, payload={}", eventIdHeader, eventType, payload);
        UUID eventId = UUID.fromString(eventIdHeader);

        // Perform the correct action depending on the type of event
        switch (eventType) {
            case "SongCreatedEvent" -> handleSongCreation(eventId, objectMapper.readValue(payload, SongCreatedEvent.class));
            case "SongDeletedEvent" -> handleSongDeletion(eventId, objectMapper.readValue(payload, SongDeletedEvent.class));
            default -> log.info("Unknown event type received. eventType={}", eventType);
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

        Song song = songRepository.findById(event.getId()).orElse(null);

        if (song != null) {
            // Case that song already exists: upsertion

            log.debug("Upserting existing song for event {}.", eventId);
            songMapper.updateEntityFromCreationEvent(event, song);

            songRepository.save(song);
        } else  {
            // Case that song doesn't exist: save as new

            log.debug("Creating song for event {}.", eventId);

            song = songRepository.save(songMapper.toEntity(event));
        }

        if (song.getArtistOwnerName() == null)
            log.warn("No artist owner name found for event {}.", eventId);
    }

    /**
     * Handles the removal of a song after a SongDeletedEvent is received.
     * <p>If the song doesn't exist, this does nothing aside from a
     * debug log, since deletion is idempotent by nature.</p>
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleSongDeletion(UUID eventId, SongDeletedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "SongDeletedEvent", event.getId().toString())) return;

        if (songRepository.existsById(event.getId()))
            log.debug("Deleting song for event {}. songId={}", eventId, event.getId());
        else
            log.debug("Song already absent from projection, nothing to delete. eventId={}, songId={}", eventId, event.getId());

        songRepository.deleteById(event.getId());
    }
}

