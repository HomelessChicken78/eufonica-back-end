package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.model.Playlist;
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
     */
    void addEntry(UUID songId, Playlist playlist);
}
