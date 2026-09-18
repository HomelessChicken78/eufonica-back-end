package it.eufonica.catalogcommandservice.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

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
        LocalDateTime start = now();
        int successfulEvents = 0;
        int failedEvents = 0;

        for (OutboxEvent event : events) {
            try {
                outboxService.markAsProcessing(event);

                ProducerRecord<String, String> record = new ProducerRecord<>(
                        event.getTopic(), event.getAggregateId(), event.getPayload());
                record.headers().add("eventId", event.getId().toString().getBytes()); // Add the event id to the headers
                record.headers().add("eventType", event.getEventType().getBytes());
                kafkaTemplate.send(record).get();

                outboxService.markAsPublished(event);
                successfulEvents++;
            } catch (Exception e) {
                failedEvents++;
                outboxService.handleFailure(event, e);
            }
        }

        if (successfulEvents + failedEvents > 0)
            log.debug("Outbox scheduled job completed in {}ms. successfulEvents={} failedEvents={}", Duration.between(start, now()).toMillis(), successfulEvents, failedEvents);
        else
            log.trace("Outbox scheduled job completed. No event sent.");
    }
}
