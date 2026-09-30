package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.artistrequest.*;
import it.eufonica.authservice.exception.client.ConflictException;
import it.eufonica.authservice.exception.client.NotFoundException;
import it.eufonica.authservice.exception.server.InternalServerErrorException;
import it.eufonica.authservice.exception.server.NotImplementedException;
import it.eufonica.authservice.mapper.ArtistRequestMapper;
import it.eufonica.authservice.model.AccessMethod;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.model.ArtistAuthProjection;
import it.eufonica.authservice.model.ArtistRequest;
import it.eufonica.authservice.repository.AccessMethodRepository;
import it.eufonica.authservice.repository.AppUserRepository;
import it.eufonica.authservice.repository.ArtistRepository;
import it.eufonica.authservice.repository.ArtistRequestRepository;
import it.eufonica.authservice.security.CognitoUserService;
import it.eufonica.authservice.security.CurrentUserProvider;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service @Transactional
@AllArgsConstructor @Slf4j
public class ArtistRequestServiceImpl implements ArtistRequestService {
    // Repositories
    private final ArtistRequestRepository requestRepository;
    private final ArtistRepository artistRepository;
    private final AppUserRepository userRepository;
    private final AccessMethodRepository accessMethodRepository;

    // Mapper & Utility
    private final ArtistRequestMapper requestMapper;
    private final CurrentUserProvider currentUserProvider;

    // Services
    private final CognitoUserService cognitoUserService;

    private void validateArtistRequest(AppUser currentUser) {
        if (currentUser.getAffiliatedArtist() != null)
            throw new ConflictException("You appear to already be an artist.");

        if (requestRepository.existsByRequestingUserAndStatus(currentUser, ArtistRequest.RequestStatus.PENDING))
            throw new ConflictException("You already have an artist request pending. Please wait for an admin to evaluate that first.");

        // [V.ArtistRequest.richiesta_dopo_registrazione_ut] is structurally guaranteed:
        // the request's timestamp is set by Hibernate (@CreationTimestamp) at save time,
        // strictly after the current moment used here, and a user must already exist
        // (and therefore be registered) to reach this point. No runtime check needed.
    }

    private List<ArtistRequestShortResponseDTO> doSearch(ArtistRequestFiltersDTO filters, Integer pageNumber, Integer pageSize) {
        Specification<ArtistRequest> spec = buildSpecification(filters);

        return requestRepository.findAll(spec, PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "timestamp")))
                .stream()
                .map(requestMapper::toShortResponse)
                .toList();
    }

    private Specification<ArtistRequest> buildSpecification(ArtistRequestFiltersDTO filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            CommonArtistRequestFiltersDTO commonFilters = filters.getCommonFilters();

            // Status
            if (commonFilters.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), commonFilters.getStatus()));
                log.trace("Added filter status = {}.", commonFilters.getStatus());
            }

            // Since
            if (commonFilters.getSince() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), commonFilters.getSince().atStartOfDay()));
                log.trace("Added filter timestamp >= {}.", commonFilters.getSince().atStartOfDay());
            }

            // Requesting user
            if (filters.getRequestingUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("requestingUser").get("id"), filters.getRequestingUserId()));
                log.trace("Added filter requestingUserId = {}.", filters.getRequestingUserId());
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

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
        AppUser currentUser = currentUserProvider.getCurrentUser();

        validateArtistRequest(currentUser);

        ArtistRequest artistRequest = requestMapper.toEntity(request);
        artistRequest.setRequestingUser(currentUser);

        ArtistRequest saved = requestRepository.save(artistRequest);
        return requestMapper.toFullResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public ArtistRequestFullResponseDTO sendExistingArtistRequest(SendExistingArtistRequestDTO request) {
        AppUser currentUser = currentUserProvider.getCurrentUser();

        validateArtistRequest(currentUser);

        ArtistAuthProjection artist = artistRepository.findById(request.getArtistId())
                .orElseThrow(() -> new NotFoundException("Artist with the given id (" + request.getArtistId() + ") does not exist."));

        // [V.ArtistRequest.richiesta_dopo_registrazione_art]
        if (!artist.getRegistrationDate().isBefore(LocalDateTime.now()))
            throw new ConflictException("A request cannot concern an artist who registered after the request itself.");

        ArtistRequest artistRequest = requestMapper.toEntity(request);
        artistRequest.setRequestedArtist(artist);
        artistRequest.setRequestingUser(currentUser);

        ArtistRequest saved = requestRepository.save(artistRequest);
        return requestMapper.toFullResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ArtistRequestFullResponseDTO evaluateRequest(UUID requestId, boolean accepted) {
        AppUser evaluator = currentUserProvider.getCurrentUser();
        ArtistRequest artistRequest = findByIdOrThrow(requestId);

        if (artistRequest.getStatus() != ArtistRequest.RequestStatus.PENDING)
            throw new ConflictException("This request has already been evaluated.");

        AppUser requestingUser = artistRequest.getRequestingUser();
        ArtistAuthProjection artist = artistRequest.getRequestedArtist();

        String sub = accessMethodRepository.findByUser(requestingUser)
                .orElseThrow(() ->
                        // This shouldn't normally happen: each user must be created after registering (so AccessMethod is always created)
                        new InternalServerErrorException("User " + requestingUser.getId() + " has no associated access method."))
                .getProviderUserId();

        // If the admin accept the request, create the link and, if needed, the artist
        if (accepted) {
            log.info("Artist request with requestId={} got accepted by {} (userId={})",
                    requestId, evaluator.getDisplayName(), evaluator.getId());
            artistRequest.setStatus(ArtistRequest.RequestStatus.ACCEPTED);

            if (artist != null) {
                // Artist already exists

                log.info("Linking userId={} to artistId={} after request with requestId={} got accepted.",
                        requestingUser.getId(), artist.getId(), requestId);
                requestingUser.setAffiliatedArtist(artist);

                userRepository.save(requestingUser);
                cognitoUserService.setAffiliatedArtist(sub, artist.getId());
            }

            else {
                // Artist does not exist and must be created
                log.info("Creating new artist (name={}, affiliatedUserId={}) after request with requestId={} got accepted.",
                        artistRequest.getRequestedName(), requestingUser.getId(), requestId);
                throw new NotImplementedException("The system does not currently support creating a new artist.");
                // TODO Create the artist
                // TODO cognitoUserService.setAffiliatedArtist(sub, ...);
            }
        }

        // If the admin doesn't accept the request, don't do anything other than setting it to REJECTED
        else {
            log.info("Artist request with requestId={} got rejected by {} (userId={})",
                    requestId, evaluator.getDisplayName(), evaluator.getId());
            artistRequest.setStatus(ArtistRequest.RequestStatus.REJECTED);
        }

        ArtistRequest saved = requestRepository.save(artistRequest);
        return requestMapper.toFullResponse(saved);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<ArtistRequestShortResponseDTO> searchAllRequests(ArtistRequestFiltersDTO filters, Integer pageNumber, Integer pageSize) {
        return doSearch(filters, pageNumber, pageSize);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public List<ArtistRequestShortResponseDTO> searchOwnRequests(CommonArtistRequestFiltersDTO filters, Integer pageNumber, Integer pageSize) {
        ArtistRequestFiltersDTO completeFilters = ArtistRequestFiltersDTO.builder()
                .requestingUserId(currentUserProvider.getCurrentUser().getId())
                .commonFilters(filters)
                .build();

        return doSearch(completeFilters, pageNumber, pageSize);
    }

    @Override
    public ArtistRequestFullResponseDTO findRequest(UUID requestId) {
        ArtistRequest request = findByIdOrThrow(requestId);
        AppUser currentUser = currentUserProvider.getCurrentUser();

        boolean isOwner = request.getRequestingUser().getId().equals(currentUser.getId());

        if (!currentUserProvider.isAdmin() && !isOwner) {
            log.debug("Access denied to artist request {}: user {} is neither the owner nor an admin.",
                    requestId, currentUser.getId());
            // Deliberately throw a 404 instead a 401 to not reveal the existence of the artist request
            throw new NotFoundException("Artist request with the given id (" + requestId + ") does not exist.");
        }

        return requestMapper.toFullResponse(request);
    }
}
