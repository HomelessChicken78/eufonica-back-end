package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.playlist.PlaylistCreateRequestDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistCreatedResponseDTO;
import it.eufonica.exception.client.NotFoundException;

import java.util.UUID;

public interface PlaylistCommandService {
    /**
     * Create a new playlist. The songs in the playlist are appended in an arbitrary order.
     *
     * @param request the data for the playlist creation
     *
     * @return a dto that contains the id of the newly created playlist.
     */
    PlaylistCreatedResponseDTO createPlaylist(PlaylistCreateRequestDTO request);

    /**
     * Renames the playlist.
     *
     * @param playlistId the id of the playlist to rename
     * @param newName the new name to apply to the playlist
     *
     * @throws NotFoundException if the playlist does not exist
     */
    void rename(UUID playlistId, String newName);
}
