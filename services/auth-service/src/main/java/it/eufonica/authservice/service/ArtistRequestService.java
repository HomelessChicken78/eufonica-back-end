package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.artistrequest.*;
import it.eufonica.authservice.model.ArtistRequest;

import java.util.List;
import java.util.UUID;

public interface ArtistRequestService {
    ArtistRequest findByIdOrThrow(UUID id);

    ArtistRequestFullResponseDTO sendNewArtistRequest(SendNewArtistRequestDTO request);

    ArtistRequestFullResponseDTO sendExistingArtistRequest(SendExistingArtistRequestDTO request);

    ArtistRequestResultDTO evaluateRequest(UUID requestId, boolean accepted);

    List<ArtistRequestShortResponseDTO> searchAllRequests(ArtistRequestFiltersDTO filters, int pageNumber, int pageSize);

    List<ArtistRequestShortResponseDTO> searchOwnRequests(CommonArtistRequestFiltersDTO filters, int pageNumber, int pageSize);

    ArtistRequestFullResponseDTO findRequest(UUID requestId);
}
