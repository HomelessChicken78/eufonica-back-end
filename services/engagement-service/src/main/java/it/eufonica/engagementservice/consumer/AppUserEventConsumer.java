package it.eufonica.engagementservice.consumer;

import it.eufonica.engagementservice.mapper.AppUserMapper;
import it.eufonica.engagementservice.repository.AppUserRepository;
import it.eufonica.event.appuser.AppUserCreatedEvent;
import it.eufonica.event.processing.EventProcessingService;
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
public class AppUserEventConsumer {
    // Utility & Mappers
    private final ObjectMapper objectMapper;
    private final AppUserMapper userMapper;

    // Services
    private final EventProcessingService processingService;

    // Repositories
    private final AppUserRepository userRepository;

    @KafkaListener(topics = "${APP_USER_TOPIC_NAME:appuser.events}")
    public void consume(@Payload String payload,
                               @Header("eventId") String eventIdHeader, @Header("eventType") String eventType) {
        log.debug("Received event for app user. eventId={}, eventType={}, payload={}", eventIdHeader, eventType, payload);
        UUID eventId = UUID.fromString(eventIdHeader);

        // Perform the correct action depending on the type of event
        if (!"AppUserCreatedEvent".equals(eventType)) {
            log.warn("Unknown event type received. eventType={}", eventType);
            return;
        }

        handleAppUserCreation(eventId, objectMapper.readValue(payload, AppUserCreatedEvent.class));
    }

    /**
     * Handles the creation of a new user after a AppUserCreatedEvent is received.
     *
     * @param eventId The id of the event to compare against processed events
     * @param event The deserialized event's payload
     */
    public void handleAppUserCreation(UUID eventId, AppUserCreatedEvent event) {
        if (!processingService.saveOrIgnore(eventId, "AppUserCreatedEvent", event.getId().toString())) return;

        userRepository.save(userMapper.toEntity(event));
    }
}
