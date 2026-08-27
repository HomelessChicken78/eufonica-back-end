package it.musicplatform.catalogcommandservice.service;

import it.musicplatform.catalogcommandservice.dto.artist.*;

import java.util.UUID;

public interface ArtistCommandService {
    /**
     * Create a new Artist.
     */
    ArtistSummaryResponseDTO createArtist(ArtistCreationRequestDTO creationRequestDTO);

    /**
     * Updates Artist's data.
     */
    ArtistSummaryResponseDTO updateArtist(UUID artistId, ArtistCreationRequestDTO creationRequestDTO);
}
