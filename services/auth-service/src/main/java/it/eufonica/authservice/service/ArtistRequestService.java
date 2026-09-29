package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.artistrequest.ArtistRequestFullResponseDTO;
import it.eufonica.authservice.dto.artistrequest.ArtistRequestShortResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ArtistRequestService {
    ArtistRequestFullResponseDTO sendNewArtistRequest();

    ArtistRequestFullResponseDTO sendExistingArtistRequest();

    ArtistRequestFullResponseDTO evaluateRequest();

    List<ArtistRequestShortResponseDTO> searchAllRequests();

    List<ArtistRequestShortResponseDTO> searchOwnRequests(UUID requestingUserId);

    ArtistRequestFullResponseDTO findRequest();
}
