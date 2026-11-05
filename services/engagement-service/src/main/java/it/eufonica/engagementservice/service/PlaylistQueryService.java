package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.playlist.PlaylistResponseSortOrder;
import it.eufonica.engagementservice.dto.playlist.PlaylistSearchFiltersDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistShortResponseDTO;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.exception.client.BadRequestException;
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
     * Searches public playlists matching the given filters.
     *
     * @param filters the search filters to use
     * @param sortOrder the criteria to sort the result
     * @param pageNumber the page number to get (starts from 1)
     * @param pageSize the requested page size, with a maximum configured
     *
     * @throws BadRequestException if either the pageNumber or the pageSize are less than 1
     * @return the paginated search result
     */
    PageResponseDTO<PlaylistShortResponseDTO> searchPlaylists(PlaylistSearchFiltersDTO filters, PlaylistResponseSortOrder sortOrder,
                                                              Integer pageNumber, Integer pageSize);
}
