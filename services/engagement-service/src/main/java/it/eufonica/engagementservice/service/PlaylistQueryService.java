package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.entry.PlaylistEntrySortOrder;
import it.eufonica.engagementservice.dto.playlist.PlaylistResponseDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistSortOrder;
import it.eufonica.engagementservice.dto.playlist.PlaylistSearchFiltersDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistShortResponseDTO;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.exception.client.BadRequestException;
import it.eufonica.exception.client.ForbiddenException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.web.dto.PageResponseDTO;

import java.util.UUID;

public interface PlaylistQueryService {
    /**
     * Finds a playlist by its id.
     *
     * @param id the unique id of the playlist
     *
     * @return the playlist associated with the given id
     * @throws NotFoundException if no playlist exists with the given id
     */
    Playlist findOrElseThrow(UUID id);

    /**
     * Searches playlists matching the given filters.
     *
     * <p>Can only return {@code PUBLIC} playlists, unless {@code filters.showOnlyUnlisted}
     * is {@code true} (filters admin-only).
     *
     * @param filters the search filters to use
     * @param sortOrder the criteria to sort the result
     * @param pageNumber the page number to get (starts from 1)
     * @param pageSize the requested page size, with a maximum configured
     *
     * @throws BadRequestException if either the pageNumber or the pageSize are less than 1
     * @throws ForbiddenException if a non-admin tries to call the method
     * with {@code filters.showOnlyUnlisted = true}
     * @return the paginated search result
     */
    PageResponseDTO<PlaylistShortResponseDTO> searchPlaylists(PlaylistSearchFiltersDTO filters, PlaylistSortOrder sortOrder,
                                                              Integer pageNumber, Integer pageSize);

    /**
     * Finds the playlist with the given {@code playlistId}. Returns all of its entries,
     * paginated and sorted with the criteria defined in {@code sortOrder}.
     *
     * @param playlistId the id of the playlist to find
     * @param sortOrder the sorting criteria of the entry inside the playlist
     * @param pageNumber the page number to get (starts from 1)
     * @param pageSize the requested page size, with a maximum configured
     *
     * @return the playlist with the paginated entries
     * @throws BadRequestException if either the pageNumber or the pageSize are less than 1
     * @throws NotFoundException if either the playlist does not exist, or the caller is not
     * the playlist owner and the playlist is {@code PRIVATE}
     * @throws ForbiddenException if the caller is not the playlist owner (ADMIN included,
     * no bypass for this operation) and the playlist is not {@code PRIVATE}
     */
    PlaylistResponseDTO findPlaylist(UUID playlistId, PlaylistEntrySortOrder sortOrder,
                                     Integer pageNumber, Integer pageSize);
}
