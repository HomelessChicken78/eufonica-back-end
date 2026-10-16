package it.eufonica.catalogcommandservice.service.core;

import it.eufonica.catalogcommandservice.dto.artist.ArtistCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.artist.ArtistSummaryResponseDTO;
import it.eufonica.exception.client.ForbiddenException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.catalogcommandservice.model.Artist;

import java.util.UUID;

public interface ArtistCommandService {
    /**
     * Finds an artist by its id.
     *
     * @param id the unique id of the artist
     * @return the artist associated with the given id
     * @throws NotFoundException if no artist exists with the given id
     */
    Artist findByIdOrThrow(UUID id);

    /**
     * Create a new Artist.
     *
     * @param creationRequestDTO the request to create an artist
     * @return the created artist
     */
    ArtistSummaryResponseDTO createArtist(ArtistCreationRequestDTO creationRequestDTO);

    /**
     * Updates the data of the artist matching the caller's own artist affiliation.
     *
     * @param creationRequestDTO the updated artist data
     *
     * @return the updated artist
     * @throws ForbiddenException if the caller has no artist affiliation
     */
    ArtistSummaryResponseDTO updateArtist(ArtistCreationRequestDTO creationRequestDTO);
}
