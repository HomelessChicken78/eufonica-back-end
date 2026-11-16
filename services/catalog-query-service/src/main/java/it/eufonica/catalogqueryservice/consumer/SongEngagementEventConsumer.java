package it.eufonica.catalogqueryservice.consumer;

import it.eufonica.catalogqueryservice.model.SongRead;
import it.eufonica.catalogqueryservice.repository.SongRepository;
import it.eufonica.event.processing.EventProcessingService;
import it.eufonica.event.song.SongLikedEvent;
import it.eufonica.event.song.SongUnlikedEvent;
import it.eufonica.exception.client.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
@RequiredArgsConstructor @Slf4j
public class SongEngagementEventConsumer {
    // Service
    private final EventProcessingService processingService;

    // Repository
    private final SongRepository songRepository;

    // Utility & Mappers
    private final ObjectMapper objectMapper;
    private final RedisCacheManager cacheManager;

    /**
     * Removes a song from the "songs" cache.
     * <p>
     * We do this manually instead of using @CacheEvict on each handler method,
     * because the handlers are called from consume() in the SAME class
     * (self-invocation). Spring's caching annotations only work when a method
     * is called from OUTSIDE the class, through Spring's proxy. Calling a
     * method directly on "this" skips that proxy, so @CacheEvict would
     * silently do nothing here.
     *
     * @param songId the id of the song to remove from the cache
     */
    private void evictSongCache(UUID songId) {
        var cache = cacheManager.getCache("songs");
        if (cache != null) cache.evict(songId);
    }

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

        SongRead song = songRepository
                .findById(event.getSongId())
                .orElseThrow(
                        () -> new NotFoundException("No song found with id: " + event.getSongId() + ".")
                );

        song.setAmountLikes(song.getAmountLikes() + 1);

        songRepository.save(song);
        evictSongCache(event.getSongId());
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

        SongRead song = songRepository
                .findById(event.getSongId())
                .orElseThrow(
                        () -> new NotFoundException("No song found with id: " + event.getSongId() + ".")
                );

        song.setAmountLikes(song.getAmountLikes() - 1);

        songRepository.save(song);
        evictSongCache(event.getSongId());
    }
}
