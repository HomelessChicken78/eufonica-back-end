package it.eufonica.engagementservice.security;

import it.eufonica.engagementservice.exception.EngagementErrorReason;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.VisibilityType;
import it.eufonica.exception.CommonErrorReason;
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
     * Checks whether the caller is the playlist's owner, after handling the conditions that
     * short-circuit regardless of ownership status.
     *
     * @return {@code true} if the caller is the playlist's owner, {@code false} otherwise
     *
     * @throws IllegalArgumentException if playlist is {@code null}
     * @throws NotFoundException if the caller's local AppUser projection hasn't been
     * synced yet, or if the playlist is {@code PRIVATE} and the caller is not the owner
     */
    private boolean isOwnerOrThrowIfHidden(Playlist playlist) {
        if (playlist == null)
            throw new IllegalArgumentException("Playlist parameter cannot be null.");

        UUID currentUserId = currentUser.getCurrentUser().getId();

        if (currentUserId.equals(playlist.getCreator().getId())) {
            log.debug("User is the owner of the playlist. userId={}, playlistId={}", currentUserId, playlist.getId());
            return true;
        }

        // Deliberately throw a 404 instead a 403 to not reveal the existence of the playlist
        if (playlist.getVisibility() == VisibilityType.PRIVATE)
            throw new NotFoundException("Could not find playlist with id " + playlist.getId() + ".", CommonErrorReason.RESOURCE_NOT_FOUND);

        return false;
    }

    /**
     * Checks whether the playlist is not {@code PRIVATE} or, if {@code PRIVATE}, checks
     * if the caller is the playlist's owner
     *
     * @throws IllegalArgumentException if playlist is {@code null}
     * @throws NotFoundException if the caller's local AppUser projection hasn't been
     * synced yet, or if the playlist is {@code PRIVATE} and the caller is not the owner
     */
    public void requireViewable(Playlist playlist) {
        isOwnerOrThrowIfHidden(playlist);
    }

    /**
     * Ensures the caller is authenticated and identified with the id corresponding
     * to the playlist's owner ({@code playlist.creator.id}).
     *
     * @param playlist the playlist the caller has to be owner of
     *
     * @throws IllegalArgumentException if playlist is {@code null}
     * @throws NotFoundException if either of these conditions is {@code true}:
     * <ul>
     *     <li>The caller's local {@code AppUser} projection hasn't been synced yet.</li>
     *     <li>The playlist is {@code PRIVATE} and the caller is not the playlist's owner.
     *     This hides the existence of the playlist to a non-owner.</li>
     * </ul>
     * @throws ForbiddenException if the playlist is not {@code PRIVATE} and the caller
     * is not the playlist's owner
     */
    public void requireOwner(Playlist playlist) {
        if (isOwnerOrThrowIfHidden(playlist)) return;

        // The playlist existence is known. Throwing a 403 is safe
        throw new ForbiddenException("The action cannot be performed on the playlist with id " + playlist.getId() + ".", EngagementErrorReason.NOT_PLAYLIST_OWNER);
    }

    /**
     * Ensures that either the caller is authenticated and identified with the id
     * corresponding to the playlist's owner ({@code playlist.creator.id} or the caller
     * is an ADMIN and the playlist is not {@code PRIVATE}.
     *
     * @param playlist the playlist from which extract the owner and the visibility to
     * perform ownership checks.
     *
     * @throws IllegalArgumentException if playlist is {@code null}
     * @throws NotFoundException if either of these conditions is {@code true}:
     * <ul>
     *     <li>The caller's local {@code AppUser} projection hasn't been synced yet.</li>
     *     <li>The playlist is {@code PRIVATE} and the caller is not the playlist's owner.
     *     This hides the existence of the playlist to a non-owner.</li>
     * </ul>
     * @throws ForbiddenException if the playlist is not {@code PRIVATE} and the caller
     * is not the playlist's owner or is not an ADMIN
     */
    public void requireOwnerOrAdmin(Playlist playlist) {
        if (isOwnerOrThrowIfHidden(playlist)) return;

        // If visibility is not PRIVATE, check if it's an admin
        if (!currentCaller.isAdmin())
            // The playlist existence is known. Throwing a 403 is safe
            throw new ForbiddenException("The action cannot be performed on the playlist with id " + playlist.getId() + ".", EngagementErrorReason.NOT_PLAYLIST_OWNER);

        log.info("Admin is performing an action on a playlist. userId={}, playlistId={}", playlist.getCreator().getId(), playlist.getId());
    }
}
