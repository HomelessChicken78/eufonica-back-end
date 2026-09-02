package it.musicplatform.catalogcommandservice.service;

import it.musicplatform.catalogcommandservice.dto.artist.*;
import it.musicplatform.catalogcommandservice.exception.client.NotFoundException;
import it.musicplatform.catalogcommandservice.model.Artist;

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
     * Updates Artist's data.
     *
     * @param artistId the unique id of the artist
     * @param creationRequestDTO the request to create an artist
     * @return the updated artist
     */
    ArtistSummaryResponseDTO updateArtist(UUID artistId, ArtistCreationRequestDTO creationRequestDTO);
}
