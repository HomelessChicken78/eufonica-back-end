package it.eufonica.catalogcommandservice.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Integer> {
    @Query(
            """
            SELECT evt
            FROM OutboxEvent AS evt
            WHERE evt.nextAttemptAt <= :instant
            AND evt.status IN ('PENDING', 'PROCESSING')
            """
    )
    List<OutboxEvent> findEventsReadyForProcessing(@Param("instant") LocalDateTime instant);
}
