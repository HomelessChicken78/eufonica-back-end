package it.eufonica.engagementservice.security;

import it.eufonica.engagementservice.exception.EngagementErrorReason;
import it.eufonica.engagementservice.model.AppUser;
import it.eufonica.engagementservice.repository.AppUserRepository;
import it.eufonica.exception.client.ForbiddenException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.exception.server.InternalServerErrorException;
import it.eufonica.security.CurrentCallerProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component @RequiredArgsConstructor @Slf4j
public class CurrentUserResolver {
    // Repository
    private final AppUserRepository userRepository;

    // Utility & Mappers
    private final CurrentCallerProvider currentCaller;

    /**
     * Retrieves the application user associated with the current security context.
     *
     * @return the {@link AppUser} of the currently authenticated caller
     * @throws NotFoundException if the caller's local AppUser projection hasn't been synced yet
     */
    public AppUser getCurrentUser() {
        String callerSub = currentCaller.getSub();

        AppUser found = userRepository.findBySub(callerSub)
                .orElseThrow(
                        () -> new NotFoundException("User could not be found.", EngagementErrorReason.USER_NOT_YET_SYNCED)
                );

        log.debug("Current user found. userId={}", found.getId());
        return found;
    }

    /**
     * Ensures the caller is authenticated and identified by the given id.
     *
     * @param userId the user id the caller must match
     *
     * @throws NotFoundException if the caller's local AppUser projection hasn't been synced yet
     * @throws ForbiddenException if the caller's id is different from {@code userId}
     */
    public void requireSelf(UUID userId) {
        UUID callerId = getCurrentUser().getId();

        if (!userId.equals(callerId))
            throw new ForbiddenException("User is not allowed to perform this operation.", EngagementErrorReason.NOT_SELF);
    }
}
