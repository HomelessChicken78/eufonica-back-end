package it.eufonica.event.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

import static java.time.Instant.now;

@Component
@RequiredArgsConstructor @Slf4j
public class OutboxProcessor {
    private final OutboxService outboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${OUTBOX_PROCESSING_TIMEOUT:30s}")
    private Duration outboxProcessingTimeout;

    @Scheduled(fixedDelayString = "${OUTBOX_PROCESSOR_JOB_DELAY_MS:1000}")
    public void process() {
        log.trace("Outbox processing started.");

        List<OutboxEvent> events = outboxService.findEventsReadyForProcessing(now(), now().minus(outboxProcessingTimeout));

        for (OutboxEvent event : events) {
            outboxService.markAsProcessing(event);

            ProducerRecord<String, String> record = new ProducerRecord<>(
                    event.getTopic(), event.getAggregateId(), event.getPayload());
            record.headers().add("eventId", event.getId().toString().getBytes()); // Add the event id to the headers
            record.headers().add("eventType", event.getEventType().getBytes());

            kafkaTemplate.send(record)
                    .whenComplete((res, t) -> {
                        if (t == null) {
                            outboxService.markAsPublished(event);
                            log.debug("Event successfully published. eventId={}, eventType={}", event.getId(), event.getEventType());
                        } else if (t instanceof Exception exc) {
                            outboxService.handleFailure(event, exc);
                            log.debug("Event publishing unsuccessful. eventId={}, eventType={}", event.getId(), event.getEventType(), exc);
                        } else log.error("Error while sending event.", t);
                    });
        }

        if (events.isEmpty())
            log.trace("Outbox scheduled job completed. No event sent.");
    }
}
