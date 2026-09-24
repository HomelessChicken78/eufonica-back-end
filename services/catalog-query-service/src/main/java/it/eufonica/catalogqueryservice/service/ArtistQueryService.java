package it.eufonica.catalogqueryservice.service;

import it.eufonica.catalogqueryservice.dto.artist.ArtistFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.artist.ArtistSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.artist.ArtistShortResponseDTO;
import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.exception.client.NotFoundException;

import java.util.UUID;

public interface ArtistQueryService {
    /**
     * Finds an artist by its id.
     *
     * @param artistId the unique id of the artist
     * @return the full DTO representing the artist
     * @throws NotFoundException if no artist could be found with the given id
     */
    ArtistFullResponseDTO findArtistById(UUID artistId);

    /**
     * Searches artists matching the given filters.
     *
     * @param filters the search filters to use
     * @param pageNumber the page number to get (starts from 1)
     * @param pageSize the requested page size, capped to a configured maximum
     * @return the paginated search result
     */
    PageResponseDTO<ArtistShortResponseDTO> searchArtists(ArtistSearchFiltersDTO filters,
                                                          Integer pageNumber, Integer pageSize);
}