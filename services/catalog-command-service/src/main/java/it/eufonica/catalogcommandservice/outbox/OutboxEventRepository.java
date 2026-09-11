package it.eufonica.catalogcommandservice.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    @Query(
            """
            SELECT evt
            FROM OutboxEvent AS evt
            WHERE (evt.status = 'PENDING'
                        AND evt.nextAttemptAt <= :instantNow)
               OR (evt.status = 'PROCESSING'
                        AND evt.processingStartedAt <= :processingTimeout)
            """
    )
    List<OutboxEvent> findEventsReadyForProcessing(@Param("instantNow") LocalDateTime instantNow,
                                                   @Param("processingTimeout") LocalDateTime processingTimeout);
}
