package it.eufonica.engagementservice.service;

import it.eufonica.exception.client.NotFoundException;

import java.util.UUID;

public interface LikeService {
    /**
     * Likes a playlist on behalf of the user corresponding to the current caller.
     *
     * <p>If the playlist is already liked by the user, the operation is a no-op.
     *
     * <p>Create a {@code PlaylistLike} between the plaulist and the user.
     *
     * @param playlistId the id of the playlist to like
     *
     * @throws NotFoundException if either the playlist does not exist, or the caller is not
     * the playlist owner and the playlist is {@code PRIVATE}
     */
    void likePlaylist(UUID playlistId);
}
