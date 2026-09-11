package it.eufonica.catalogcommandservice.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.LocalDateTime.now;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class OutboxServiceImpl implements OutboxService {
    private final OutboxEventRepository outboxEventRepository;

    @Value("${OUTBOX_RETRY_MAX_ATTEMPTS:5}")
    private int maxAttempts;

    @Value("${OUTBOX_RETRY_INITIAL_DELAY_MS:1000}")
    private long initialDelay;

    @Value("${OUTBOX_RETRY_MAX_DELAY_MS:30000}")
    private long maxDelay;

    @Value("${OUTBOX_RETRY_BACKOFF_MULTIPLIER:1.5}")
    private double backoffMultiplier;

    @Value("${OUTBOX_RETRY_JITTER_INTENSITY_PERCENTAGE:0.2}")
    private double jitterIntensity;

    private LocalDateTime calculateNextAttemptAt(int retryCount) {
        double exponentialDelay = initialDelay * Math.pow(backoffMultiplier, retryCount);

        // Cap the exponential backoff
        double cappedDelay = Math.min(exponentialDelay, maxDelay);

        // Use jitter randomness
        double jitterFactor = 1.0 + ThreadLocalRandom.current()
                .nextDouble(-jitterIntensity, jitterIntensity);

        long finalDelayMs = Math.round(cappedDelay * jitterFactor);

        // Don't let jitter bypass maximum
        finalDelayMs = Math.min(finalDelayMs, maxDelay);

        return now().plus(finalDelayMs, ChronoUnit.MILLIS);
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        // Override passed values
        event.setStatus(OutboxEvent.OutboxStatus.PENDING);
        event.setNextAttemptAt(now());

        OutboxEvent saved = outboxEventRepository.save(event);
        log.debug("Saved OutboxEvent {}", saved);
        return saved;
    }

    @Override
    public List<OutboxEvent> findEventsReadyForProcessing(LocalDateTime instant) {
        log.trace("Finding outbox events ready for processing at instant: {}", instant);

        return outboxEventRepository.findEventsReadyForProcessing(instant);
    }

    @Override
    public void markAsProcessing(OutboxEvent event) {
        log.trace("Marking outbox events ready for PROCESSING. eventId={}", event.getId());

        event.setStatus(OutboxEvent.OutboxStatus.PROCESSING);
        outboxEventRepository.save(event);
    }

    @Override
    public void markAsPending(OutboxEvent event) {
        log.trace("Marking outbox events as PENDING. eventId={}", event.getId());

        event.setStatus(OutboxEvent.OutboxStatus.PENDING);
        outboxEventRepository.save(event);
    }

    @Override
    public void markAsPublished(OutboxEvent event) {
        LocalDateTime publishedAt = now();
        log.trace("Marking outbox events as PUBLISHED. eventId={}, publishedAt={}", event.getId(), publishedAt);

        event.setStatus(OutboxEvent.OutboxStatus.PUBLISHED);
        event.setPublishedAt(publishedAt);
        outboxEventRepository.save(event);
    }

    @Override
    public void handleFailure(OutboxEvent event, Exception exc) {
        int updatedRetryCount = event.getRetryCount() + 1;
        event.setRetryCount(updatedRetryCount);
        event.setLastError(exc.getMessage());

        if (updatedRetryCount >= maxAttempts) {
            log.warn("Outbox event processing failed and maximum retry attempts reached. " +
                            "Marking event as FAILED. eventId={}, attempts={}",
                    event.getId(), updatedRetryCount, exc);
            event.setStatus(OutboxEvent.OutboxStatus.FAILED);
        }
        else {
            event.setStatus(OutboxEvent.OutboxStatus.PENDING);

            LocalDateTime nextAttemptAt = calculateNextAttemptAt(updatedRetryCount);
            event.setNextAttemptAt(nextAttemptAt);

            log.warn("Outbox event processing failed. eventId={}, attempts={}, nextAttemptAt={}",
                    event.getId(), updatedRetryCount, nextAttemptAt, exc);
        }

        outboxEventRepository.save(event);
    }
}
