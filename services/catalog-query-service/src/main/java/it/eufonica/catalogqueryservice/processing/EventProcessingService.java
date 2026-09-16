package it.eufonica.catalogqueryservice.processing;

import java.util.UUID;

public interface EventProcessingService {
    /**
     * Tries to save the event in the ProcessedEvent table.
     * If it can't be saved, the event must have been already processed
     * and should be ignored.
     *
     * @param eventId The id of the event to save
     * @param eventType The type of the event to save (e.g. {@code SongCreatedEvent}) - used for testing/statistics
     * @param aggregateId The id of the event's aggregate - used for testing/statistics
     * @return <ul>
     *     <li>{@code true} if the event can be saved (there is no duplicate event)</li>
     *     <li>{@code false} if the event can't be saved (the event has been already processed before)</li>
     * </ul>
     */
    boolean saveOrIgnore(UUID eventId, String eventType, String aggregateId);
}
