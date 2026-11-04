package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.playlist.PlaylistResponseSortOrder;
import it.eufonica.engagementservice.dto.playlist.PlaylistSearchFiltersDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistShortResponseDTO;
import it.eufonica.exception.client.BadRequestException;
import it.eufonica.web.dto.PageResponseDTO;

public interface PlaylistQueryService {
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
