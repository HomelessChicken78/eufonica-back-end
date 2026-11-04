package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.playlist.PlaylistCreateRequestDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistCreatedResponseDTO;

public interface PlaylistCommandService {
    /**
     * Create a new playlist. The songs in the playlist are appended in an arbitrary order.
     *
     * @param request The data for the playlist creation
     *
     * @return a dto that contains the id of the newly created playlist.
     */
    PlaylistCreatedResponseDTO createPlaylist(PlaylistCreateRequestDTO request);
}
