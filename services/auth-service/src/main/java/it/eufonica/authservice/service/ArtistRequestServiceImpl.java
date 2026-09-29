package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.artistrequest.*;
import it.eufonica.authservice.exception.client.NotFoundException;
import it.eufonica.authservice.mapper.ArtistRequestMapper;
import it.eufonica.authservice.model.ArtistRequest;
import it.eufonica.authservice.repository.ArtistRequestRepository;
import it.eufonica.authservice.security.CurrentUserProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service @Transactional
@AllArgsConstructor @Slf4j
public class ArtistRequestServiceImpl implements ArtistRequestService {
    // Repositories
    private final ArtistRequestRepository requestRepository;

    // Mapper & Utility
    private final ArtistRequestMapper requestMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public ArtistRequest findByIdOrThrow(UUID id) {
        ArtistRequest found = requestRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Artist request with the given id (" + id + ") does not exist.")
                );

        log.trace("findByIdOrThrow - found {}", found);
        return found;
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public ArtistRequestFullResponseDTO sendNewArtistRequest(SendNewArtistRequestDTO request) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public ArtistRequestFullResponseDTO sendExistingArtistRequest(SendExistingArtistRequestDTO request) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ArtistRequestFullResponseDTO evaluateRequest(UUID requestId, boolean accepted) {
        return null;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<ArtistRequestShortResponseDTO> searchAllRequests(SearchArtistRequestFiltersDTO filters, Integer pageNumber, Integer pageSize) {
        return List.of();
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public List<ArtistRequestShortResponseDTO> searchOwnRequests(UUID requestingUserId, Integer pageNumber, Integer pageSize) {
        return List.of();
    }

    @Override
    public ArtistRequestFullResponseDTO findRequest(UUID requestId) {
        return null;
    }
}
