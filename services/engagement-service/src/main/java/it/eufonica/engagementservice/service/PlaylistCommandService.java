package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.playlist.PlaylistCreateRequestDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistCreatedResponseDTO;
import it.eufonica.engagementservice.model.VisibilityType;
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

    /**
     * Sets the playlist's visibility to {@code newVisibility}.
     *
     * <p>Available values are:
     * <ul>
     *     <li>{@code PRIVATE}: the playlist can only be seen by its owner.</li>
     *     <li>{@code UNLISTED}: the playlist can only be found through its id. Search method(s) don't list it in most cases.</li>
     *     <li>{@code PUBLIC}: the playlist can be seen by everyone and found through search method(s).</li>
     * </ul>
     *
     * @param playlistId the id of the playlist to change the visibility of
     * @param newVisibility the new visibility of the playlist
     *
     * @throws NotFoundException if the playlist does not exist
     */
    void setVisibility(UUID playlistId, VisibilityType newVisibility);
}
