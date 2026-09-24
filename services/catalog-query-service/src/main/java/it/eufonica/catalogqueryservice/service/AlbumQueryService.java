package it.eufonica.catalogqueryservice.service;

import it.eufonica.catalogqueryservice.dto.album.AlbumFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.album.AlbumSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.album.AlbumShortResponseDTO;
import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.exception.client.NotFoundException;

import java.util.UUID;

public interface AlbumQueryService {
    /**
     * Finds an album by its id.
     *
     * @param albumId the unique id of the song
     * @return the full DTO representing the album
     * @throws NotFoundException if no album exists with the given id
     */
    AlbumFullResponseDTO findAlbumById(UUID albumId);

    /**
     * Searches albums matching the given filters.
     *
     * @param filters the search filters to use
     * @param pageNumber the page number to get (starts from 1)
     * @param pageSize the requested page size, with a maximum configured
     * @return the paginated search result
     */
    PageResponseDTO<AlbumShortResponseDTO> searchAlbums(AlbumSearchFiltersDTO filters,
                                                        Integer pageNumber, Integer pageSize);
}
