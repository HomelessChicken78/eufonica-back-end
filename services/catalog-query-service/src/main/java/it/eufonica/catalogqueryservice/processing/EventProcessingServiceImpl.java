package it.eufonica.catalogqueryservice.processing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j @RequiredArgsConstructor
@Service @Transactional
public class EventProcessingServiceImpl implements EventProcessingService {
    private final ProcessedEventRepository eventRepository;

    @Override
    public boolean saveOrIgnore(UUID eventId, String eventType, String aggregateId) {
        try {
            ProcessedEvent processedEvent = ProcessedEvent.builder()
                    .id(eventId)
                    .eventType(eventType)
                    .aggregateId(aggregateId)
                    .build();

            // Flush immediately the changes to the db to avoid the change being "on hold" in the persistence context.
            eventRepository.saveAndFlush(processedEvent);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Catch DataIntegrityViolationException instead of propagating it.
            // This allows us to still give an ACK to kafka,
            // thus avoiding a useless retry of an event that has purposely been ignored.
            log.debug("Event already exists. eventId={}, eventType={}", eventId, eventType, e);
            return false;
        }
    }
}
