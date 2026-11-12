package it.eufonica.event.processing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {
    @Modifying
    @Query(nativeQuery = true, value =
            """
            INSERT INTO processed_event (id, event_type, aggregate_id, processed_at)
            VALUES (:id, :eventType, :aggregateId, NOW())
            ON CONFLICT (id) DO NOTHING
            """
    )
    int saveEvent(@Param("id") UUID id, @Param("eventType") String eventType, @Param("aggregateId") String aggregateId);
}
