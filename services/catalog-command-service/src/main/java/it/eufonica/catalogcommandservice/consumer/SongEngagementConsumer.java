package it.eufonica.catalogcommandservice.consumer;

import it.eufonica.catalogcommandservice.model.Song;
import it.eufonica.catalogcommandservice.repository.SongRepository;
import it.eufonica.catalogcommandservice.service.core.SongCommandService;
import it.eufonica.event.processing.EventProcessingService;
import it.eufonica.event.song.SongLikedEvent;
import it.eufonica.event.song.SongUnlikedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
@RequiredArgsConstructor @Slf4j
public class SongEngagementConsumer {
    // Service
    private final EventProcessingService processingService;
    private final SongCommandService songService;

    // Repository
    private final SongRepository songRepository;

    // Utility & Mappers
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${SONG_ENGAGEMENT_TOPIC_NAME:song-engagement.events}")
    public void consume(@Payload String payload,
                        @Header("eventId") String eventIdHeader, @Header("eventType") String eventType) {
        log.debug("Received event for song engagement. eventId={}, eventType={}, payload={}", eventIdHeader, eventType, payload);
        UUID eventId = UUID.fromString(eventIdHeader);

        // Perform the correct action depending on the type of event
        switch (eventType) {
            case "SongLikedEvent" -> handleSongLike(eventId, objectMapper.readValue(payload, SongLikedEvent.class));
            case "SongUnlikedEvent" -> handleSongUnlike(eventId, objectMapper.readValue(payload, SongUnlikedEvent.class));
            default -> log.warn("Unknown event received. eventType: {}", eventType);
        }
    }

    /*
     * We don't need to check for the order since all the events for the same aggregate end up in the same partition.
     */
    /**
     * Handles likes of a song after SongLikedEvent is received.
     *
     * @param eventId the id of the event to compare against processed events
     * @param event the deserialized event's payload
     */
    public void handleSongLike(UUID eventId, SongLikedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "SongLikedEvent", event.getSongId().toString())) return;

        Song song = songService.findByIdOrElseThrow(event.getSongId());

        song.setAmountLikes(song.getAmountLikes() + 1);

        songRepository.save(song);
    }

    /*
     * We don't need to check for the order since all the events for the same aggregate end up in the same partition.
     */
    /**
     * Handles unlike of a song after SongUnlikedEvent is received.
     *
     * @param eventId the id of the event to compare against processed events
     * @param event the deserialized event's payload
     */
    public void handleSongUnlike(UUID eventId, SongUnlikedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "SongUnlikedEvent", event.getSongId().toString())) return;

        Song song = songService.findByIdOrElseThrow(event.getSongId());

        song.setAmountLikes(song.getAmountLikes() - 1);

        songRepository.save(song);
    }
}
