package it.eufonica.event.outbox;

import java.time.Instant;
import java.util.List;

public interface OutboxService {
    OutboxEvent save(OutboxEvent event);

    List<OutboxEvent> findEventsReadyForProcessing(Instant instant, Instant processingTimeout);

    void markAsProcessing(OutboxEvent event);

    void markAsPending(OutboxEvent event);

    void markAsPublished(OutboxEvent event);

    void handleFailure(OutboxEvent event, Exception exc);
}
