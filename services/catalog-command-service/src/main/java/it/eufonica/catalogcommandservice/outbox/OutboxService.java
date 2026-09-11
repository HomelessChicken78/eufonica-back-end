package it.eufonica.catalogcommandservice.outbox;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxService {
    OutboxEvent save(OutboxEvent event);

    List<OutboxEvent> findEventsReadyForProcessing(LocalDateTime instant);

    void markAsProcessing(OutboxEvent event);

    void markAsPending(OutboxEvent event);

    void markAsPublished(OutboxEvent event);

    void handleFailure(OutboxEvent event, Exception exc);
}

