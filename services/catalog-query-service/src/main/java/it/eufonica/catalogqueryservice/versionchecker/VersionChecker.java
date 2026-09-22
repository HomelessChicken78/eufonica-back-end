package it.eufonica.catalogqueryservice.versionchecker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component @Slf4j
public class VersionChecker {
    /**
     * Compare the current aggregate's version with the version in the event.
     * Should be used for state-representing events.
     *
     * @param eventVersion the event's version
     * @param currentVersion the current aggregate's version
     *
     * @return <ul>
     *     <li>{@code true} if eventVersion <= currentVersion</li>
     *     <li>{@code false} otherwise</li>
     * </ul>
     */
    public boolean isStateRepresentationEventOutdated(Integer eventVersion, Integer currentVersion) {
        return eventVersion <= currentVersion;
    }

    /**
     * Compare the current aggregate's version with the version in the event.
     * A delta event is expected to arrive with {@code eventVersion == currentVersion + 1},
     * since it represents the state right after the current one.
     * <p>
     * Logs a warning if the event's version indicates a gap (a jump forward of more
     * than one step) or a duplicate/out-of-order delivery (an event whose version is
     * not strictly greater than the current one).
     * Should be used for domain events.
     *
     * @param eventVersion the event's version
     * @param currentVersion the current aggregate's version
     *
     * @return <ul>
     *     <li>{@code true} if the event is anomalous (a gap or an out-of-order/duplicate delivery)</li>
     *     <li>{@code false} if the event is the expected next version in sequence</li>
     * </ul>
     */
    public boolean isDeltaVersionAnomalous(Integer eventVersion, Integer currentVersion) {
        boolean anomalous = eventVersion != currentVersion + 1;

        if (anomalous) {
            if (eventVersion > currentVersion)
                log.warn("Delta event version ({}) skips ahead of the expected next version ({}). " +
                                "One or more events may have been lost or delivered out of order.",
                        eventVersion, currentVersion + 1);
            else
                log.warn("Delta event version ({}) is not newer than the current aggregate version ({}). " +
                                "This may be a duplicate or an out-of-order delivery.",
                        eventVersion, currentVersion);
        }

        return anomalous;
    }
}
