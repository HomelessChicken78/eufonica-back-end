package it.eufonica.catalogcommandservice.consumer;

import it.eufonica.catalogcommandservice.event.song.SongDeletedEvent;
import it.eufonica.catalogcommandservice.service.media.AudioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor @Slf4j
public class AudioFileDeletionConsumer {
    // Utility & Mappers
    private final ObjectMapper objectMapper;

    // Service
    private final AudioStorageService audioStorageService;

    @KafkaListener(topics = "${SONG_TOPIC_NAME:song.events}")
    public void consume(@Payload String payload, @Header("eventType") String eventType) {
        if (!"SongDeletedEvent".equals(eventType)) {
            log.trace("Event received but ignored: {}.", eventType);
            return;
        }

        String audioObjectKey = objectMapper.readValue(payload, SongDeletedEvent.class).getAudio();
        audioStorageService.delete(audioObjectKey);
    }
}
