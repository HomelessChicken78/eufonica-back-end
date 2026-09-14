package it.eufonica.catalogcommandservice.outbox;

import it.eufonica.catalogcommandservice.exception.server.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class OutboxEventPublisherServiceImpl implements OutboxEventPublisherService {
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper; // For serializing the JSON

    @Override
    public void publish(String aggregateId, String eventType, String topic, Object event) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            throw new InternalServerErrorException("Failed to serialize " + eventType + ".", e);
        }

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(aggregateId)
                .eventType(eventType)
                .topic(topic)
                .payload(payload)
                .build();

        outboxService.save(outboxEvent);
        log.debug("Put {} in outbox. aggregateId={}", eventType, aggregateId);
    }
}
