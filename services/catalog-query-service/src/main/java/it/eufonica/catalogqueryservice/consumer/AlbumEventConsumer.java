package it.eufonica.catalogqueryservice.consumer;

import it.eufonica.catalogqueryservice.event.album.*;
import it.eufonica.catalogqueryservice.processing.EventProcessingService;
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

    // Services
    private final EventProcessingService processingService;

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

    public void handleAlbumCreation(UUID eventId, AlbumCreatedEvent event) {
        // TODO unfinished stub method
    }

    public void handleAlbumSongAdded(UUID eventId, AlbumSongAddedEvent event) {
        // TODO unfinished stub method
    }

    public void handleAlbumSongRemoved(UUID eventId, AlbumSongRemovedEvent event) {
        // TODO unfinished stub method
    }

    public void handleAlbumUpdatedEvent(UUID eventId, AlbumUpdatedEvent event) {
        // TODO unfinished stub method
    }

    public void handleAlbumDeletion(UUID eventId, DeletedAlbumEvent event) {
        // TODO unfinished stub method
    }
}
