package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.entry.PlaylistEntryRepositionRequestDTO;
import it.eufonica.engagementservice.dto.entry.PlaylistEntryRepositionResponseDTO;
import it.eufonica.engagementservice.dto.entry.PlaylistEntryShortResponseDTO;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistEntry;
import it.eufonica.exception.client.BadRequestException;
import it.eufonica.exception.client.ForbiddenException;
import it.eufonica.exception.client.NotFoundException;

import java.util.UUID;

public interface PlaylistEntryService {
    /**
     * Associates the specific song with the playlist.
     *
     * <p>Creates a new PlaylistEntry containing the ids of the song and the playlist.
     * The PlaylistEntry's position is equivalent to the position of the last entry + {@code positionGap},
     * or, if the first song, {@code positionGap}.
     *
     * @param playlist the playlist to which the song is added
     * @param songId the id of the song to associate with the playlist
     *
     * @throws NotFoundException if the song with the given id does not exist
     * @return the newly created {@code PlaylistEntry} that associate the song and the playlist
     */
    PlaylistEntry addEntry(Playlist playlist, UUID songId);

    /**
     * Associates the specific song with the playlist.
     *
     * <p>Creates a new PlaylistEntry containing the ids of the song and the playlist.
     * The PlaylistEntry's position is equivalent to the position of the last entry +
     * {@code positionGap}, or, if the first song, {@code positionGap}.
     *
     * <p>Can only be performed by the owner
     *
     * @param playlistId the id of the playlist to which the song is added
     * @param songId the id of the song to associate with the playlist
     *
     * @throws NotFoundException if the song or the playlist with the given id does not exist,
     * or the caller is not the playlist's owner and the playlist is {@code PRIVATE}
     * @throws ForbiddenException if the playlist is not {@code PRIVATE} and the caller is not
     * the playlist's owner
     * @return a dto containing essential information of the newly created {@code PlaylistEntry}
     * (both the playlist entry's id and its id).
     */
    PlaylistEntryShortResponseDTO addSongPlaylist(UUID playlistId, UUID songId);

    /**
     * Delete an entry from a specific playlist.
     *
     * <p>Can only be performed by the owner
     *
     * @param playlistId the id of the playlist to delete the entry from
     * @param entryId the id of the entry to delete
     *
     * @throws NotFoundException if either the playlist does not exist, the entry does not
     * exist inside the playlist, or the caller is not the playlist owner and the playlist
     * is {@code PRIVATE}
     * @throws ForbiddenException if the caller is not the playlist owner (ADMIN included,
     * no bypass for this operation) and the playlist is not {@code PRIVATE}
     */
    void deletePlaylistEntry(UUID playlistId, UUID entryId);

    /**
     * Change the {@code rankOder} and, thereby, the {@code position} of the entry with the
     * given {@code entryId} inside of the given {@code playlistId}.
     *
     * <p>If the entry is already in the requested {@code newRankOrder}, the method is a no-op.
     *
     * <p>If the {@code position} of two entry collapses into the same value, performs a
     * rebalance, with each entry having a consistent positional gap.
     *
     * @param playlistId the id of the playlist from where search the entry
     * @param entryId the id of the entry to move inside the given playlist
     * @param request a request dto containing the requested new rank for the entry
     *
     * @return a dto including the moved entry's id, its new rank order, and, if a rebalance is
     * performed, the cascade repositions (id of each moved entry + its new rank order).
     * @throws BadRequestException if the requested new rank order is less than 1 or higher than
     * the number of entries inside the given playlist
     * @throws NotFoundException if either the playlist does not exist, the entry does not
     * exist inside the playlist, or the caller is not the playlist owner and the playlist
     * is {@code PRIVATE}
     * @throws ForbiddenException if the caller is not the playlist owner (ADMIN included,
     * no bypass for this operation) and the playlist is not {@code PRIVATE}
     */
    PlaylistEntryRepositionResponseDTO reposition(UUID playlistId, UUID entryId, PlaylistEntryRepositionRequestDTO request);
}
