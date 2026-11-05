package it.eufonica.engagementservice.security;

import it.eufonica.engagementservice.model.VisibilityType;
import it.eufonica.exception.client.ForbiddenException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.security.CurrentCallerProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor @Slf4j
public class PlaylistOwnerAccessGuard {
    private final CurrentUserResolver currentUser;
    private final CurrentCallerProvider currentCaller;

    /**
     * Ensures the caller is authenticated and identified by the given id
     * or {@code playlistVisibility} is not {@code PRIVATE} and the caller is an ADMIN.
     *
     * @param ownerId the user id the caller should match if not an ADMIN
     * @param playlistVisibility the visibility of the playlist being accessed
     *
     * @throws NotFoundException if the caller's local AppUser projection hasn't been synced yet
     * @throws ForbiddenException if the caller's id is different from {@code ownerId} and either
     * the user is not an ADMIN or {@code playlistVisibility} is {@code PRIVATE}
     */
    public void requireOwnerOrAdmin(UUID ownerId, VisibilityType playlistVisibility) {
        if (playlistVisibility != VisibilityType.PRIVATE && currentCaller.isAdmin()) {
            log.info("Action allowed, user is ADMIN and playlist is not PRIVATE.");
            return;
        }

        currentUser.requireSelf(ownerId);
    }
}
