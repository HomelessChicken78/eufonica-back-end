package it.eufonica.catalogcommandservice.service.core;

import it.eufonica.catalogcommandservice.model.Album;

public interface AlbumDeletionService {
    /**
     * Deletes the given album: clears its artist and song associations (join tables only —
     * never touches the {@code Artist} or {@code Song} entities themselves), deletes the
     * {@code Album} row, and publishes an {@code AlbumDeletedEvent}.
     * <p>
     * Performs no authorization check and no lookup: the caller is responsible for having
     * already resolved and authorized the {@code Album} to delete (e.g. via
     * {@code AlbumCommandService}'s own credited-artist check for a direct user request, or
     * as the already-authorized consequence of deleting a song that would otherwise leave
     * this album without any songs). This keeps the operation usable both for a direct,
     * user-initiated album deletion and for a system-triggered cascade, without re-applying
     * authorization rules that don't apply to the latter case.
     *
     * @param album the album to delete, already loaded and authorized by the caller
     */
    void deleteAlbum(Album album);
}
