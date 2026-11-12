package it.eufonica.event.processing;

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
        // We use a native query with "INSERT ... ON CONFLICT (id) DO NOTHING" instead of save()/saveAndFlush()
        // on the repository. This is the result of three failed attempts, each one fixing a problem while
        // creating a new one:
        // 1. save()/saveAndFlush() on a plain entity doesn't work: for a non-versioned entity with a manually
        //    assigned id (like this one), Spring Data's default isNew() check looks at whether the id is null.
        //    Since we always assign the id ourselves, isNew() always returns false, so Spring Data always calls
        //    merge() instead of persist(). merge() checks if a row with that id already exists and, if so, just
        //    performs a silent UPDATE instead of attempting an INSERT - so a duplicate event never throws, and
        //    saveOrIgnore always returns true, defeating deduplication entirely.
        // 2. Making ProcessedEvent implement Persistable<UUID> with isNew() always returning true forces
        //    persist() instead of merge(), so a duplicate id now correctly throws on INSERT. But catching that
        //    exception in Java isn't enough: when a flush fails, JPA doesn't just fail that one INSERT - it marks
        //    the WHOLE transaction as rollback-only. Catching the exception doesn't undo that marking:
        //    the transaction is still rollback-only. If saveOrIgnore shares its transaction with the caller,
        //    the method still returns normally after the catch, so Spring assumes everything went fine and tries
        //    to COMMIT, but the transaction can only be rolled back, and UnexpectedRollbackException is thrown.
        //    That exception is never caught anywhere and triggers a retry.
        // 3. Isolating the insert (via the EntityManager directly, bypassing the repository) in its own
        //    @Transactional(REQUIRES_NEW) method fixes the UnexpectedRollbackException. The insert method itself
        //    has no try-catch, so, when the INSERT fails, the exception just exits that method, and Spring's
        //    interceptor around it sees the method exit WITH an exception - so it rolls back and moves on. Only
        //    saveOrIgnore, one level up, has the try/catch, and by then that inner transaction is already closed
        //    and rolled back cleanly.
        //    (This method has to live in a separate class/bean, not just a separate method in this same class:
        //    Spring only applies @Transactional through a proxy around the whole bean, and calling another method
        //    of the SAME object from inside saveOrIgnore skips that proxy entirely, so REQUIRES_NEW would be
        //    silently ignored.)
        //    But this creates a different, worse bug: now there are two separate transactions, A (the caller's,
        //    which also runs whatever business logic needs this event) and B (just this insert). B commits the
        //    marker row on its own, independently of A. So for a genuinely new event: B inserts and commits the
        //    marker, then A continues with its own business logic - if THAT fails for any unrelated reason, A
        //    rolls back and undoes whatever it was doing, but B is already committed and stays committed
        //    regardless, since it finished and closed before A even got to that point. On retry, saveOrIgnore
        //    sees the marker from B already there and treats the event as a duplicate, even though the real work
        //    was never actually done - the event is silently dropped and nothing in the logs says so.
        // ON CONFLICT DO NOTHING avoids every one of these: it never throws on a duplicate (so no exception, no
        // rollback-only transaction, no UnexpectedRollbackException, no REQUIRES_NEW needed), and since there's no
        // separate transaction, the marker insert and whatever business logic follows it live and die together in
        // the same transaction as the caller - exactly the atomicity we need.
        if (eventRepository.saveEvent(eventId, eventType, aggregateId) != 0)
            return true;
        else {
            log.debug("Event already exists. eventId={}, eventType={}", eventId, eventType);
            return false;
        }
    }
}
