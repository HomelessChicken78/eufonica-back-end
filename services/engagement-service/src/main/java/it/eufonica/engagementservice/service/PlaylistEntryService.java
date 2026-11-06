package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.entry.PlaylistEntryShortResponseDTO;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistEntry;
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
     * @param songId the id of the song to associate with the playlist
     * @param playlist the playlist to which the song is added
     *
     * @throws NotFoundException if the song with the given id does not exist
     * @return the newly created {@code PlaylistEntry} that associate the song and the playlist
     */
    PlaylistEntry addEntry(UUID songId, Playlist playlist);

    /**
     * Associates the specific song with the playlist.
     *
     * <p>Creates a new PlaylistEntry containing the ids of the song and the playlist.
     * The PlaylistEntry's position is equivalent to the position of the last entry +
     * {@code positionGap}, or, if the first song, {@code positionGap}.
     *
     * @param songId the id of the song to associate with the playlist
     * @param playlistId the id of the playlist to which the song is added
     *
     * @throws NotFoundException if the song or the playlist with the given id does not exist,
     * or the caller is not the playlist's owner and the playlist is {@code PRIVATE}
     * @throws ForbiddenException if the playlist is not {@code PRIVATE} and the caller is not
     * the playlist's owner
     * @return a dto containing essential information of the newly created {@code PlaylistEntry}
     * (both the playlist entry's id and its id).
     */
    PlaylistEntryShortResponseDTO addSongPlaylist(UUID songId, UUID playlistId);
}
