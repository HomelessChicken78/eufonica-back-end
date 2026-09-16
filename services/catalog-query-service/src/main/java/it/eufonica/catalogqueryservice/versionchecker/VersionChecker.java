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
     * If {@code eventVersion > currentVersion} is true, logs warn
     * Should be used for domain events.
     *
     * @param eventVersion the event's version
     * @param currentVersion the current aggregate's version
     *
     * @return <ul>
     *     <li>{@code true} if the event is anomalous (eventVersion > currentVersion)</li>
     *     <li>{@code false} otherwise.</li>
     * </ul>
     */
    public boolean isDeltaVersionAnomalous(Integer eventVersion, Integer currentVersion) {
        boolean anomalous = eventVersion > currentVersion;

        if (anomalous)
            log.warn("Delta event version ({}) is ahead of current aggregate version ({}).",
                    eventVersion, currentVersion);

        return anomalous;
    }
}
