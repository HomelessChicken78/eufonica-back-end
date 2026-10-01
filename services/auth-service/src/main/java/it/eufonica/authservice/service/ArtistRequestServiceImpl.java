package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.artist.ArtistSummaryResponseDTO;
import it.eufonica.authservice.dto.artistrequest.*;
import it.eufonica.authservice.exception.client.ConflictException;
import it.eufonica.authservice.exception.client.NotFoundException;
import it.eufonica.authservice.exception.dto.GeneralErrorResponseDTO;
import it.eufonica.authservice.exception.server.InternalServerErrorException;
import it.eufonica.authservice.mapper.ArtistMapper;
import it.eufonica.authservice.mapper.ArtistRequestMapper;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.model.ArtistAuthProjection;
import it.eufonica.authservice.model.ArtistRequest;
import it.eufonica.authservice.repository.AppUserRepository;
import it.eufonica.authservice.repository.ArtistRepository;
import it.eufonica.authservice.repository.ArtistRequestRepository;
import it.eufonica.authservice.security.CognitoUserService;
import it.eufonica.authservice.security.CurrentUserProvider;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class ArtistRequestServiceImpl implements ArtistRequestService {
    // Repositories
    private final ArtistRequestRepository requestRepository;
    private final ArtistRepository artistRepository;
    private final AppUserRepository userRepository;

    // Mapper & Utility
    private final ArtistRequestMapper requestMapper;
    private final ArtistMapper artistMapper;
    private final CurrentUserProvider currentUserProvider;

    // Services
    private final CognitoUserService cognitoUserService;
    private final AccessMethodService accessMethodService;

    // Messaging
    private final RestClient catalogComRestClient;

    @Value("${ARTIST_CREATION_URI}")
    private String artistCreationUri;

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

    /**
     * Validate whether the given request has a PENDING status or not.
     *
     * @param request The request entity to check
     * @throws ConflictException If the given request is not in status PENDING
     */
    private void validateIsPending(ArtistRequest request) {
        if (request.getStatus() != ArtistRequest.RequestStatus.PENDING)
            throw new ConflictException("This request has already been evaluated.");
    }

    /**
     * Set the given request to REJECTED
     *
     * @param request The artist request to reject
     * @param admin The admin that performed the request rejection - only used for logging
     *
     * @return A dto containing the rejected artist request
     */
    private ArtistRequestResultDTO reject(ArtistRequest request, AppUser admin) {
        request.setStatus(ArtistRequest.RequestStatus.REJECTED);
        ArtistRequest saved = requestRepository.save(request);

        log.info("Artist request with requestId={} got rejected by {} (userId={})",
                request.getId(), admin.getDisplayName(), admin.getId());

        return new ArtistRequestResultDTO(requestMapper.toFullResponse(saved), false);
    }

    /**
     * Remove the creating-artist request and creates a new existing-artist request based on it.
     * Used when there is already an accepted creating-artist request with status ACCEPTED.
     *
     * @param request The request to transform into an existing-artist request
     * @param duplicate The duplicated creating-artist request, already ACCEPTED
     * @param admin The admin that tried to accept the request - only used for logging
     *
     * @return A dto containing the newly created existing-artist request
     */
    private ArtistRequestResultDTO convertToExistingArtistRequest(ArtistRequest request, ArtistRequest duplicate, AppUser admin) {
        ArtistRequest existingArtistRequest = requestMapper.convertToExistingArtistRequest(request);
        existingArtistRequest.setRequestedArtist(duplicate.getRequestedArtist());

        ArtistRequest saved = requestRepository.save(existingArtistRequest);
        requestRepository.delete(request);

        log.info("Converted request {} into existing-artist request {} (artistId={}): " +
                        "the requested name was already taken by another accepted request.",
                request.getId(), saved.getId(), saved.getRequestedArtist().getId()); // TODO add admin

        return new ArtistRequestResultDTO(requestMapper.toFullResponse(saved), true);
    }

    /**
     * Set the given request to ACCEPTED and affiliate the requesting user
     * to the existing artist in the request itself.
     *
     * @param request The request to accept
     * @param admin The admin that accepted the request - only used for logging
     *
     * @return A dto containing the accepted artist request
     */
    private ArtistRequestResultDTO acceptForExistingArtist(ArtistRequest request, AppUser admin) {
        String sub = accessMethodService.getSubFromUser(request.getRequestingUser());

        request.setStatus(ArtistRequest.RequestStatus.ACCEPTED);

        request.getRequestingUser().setAffiliatedArtist(request.getRequestedArtist());
        userRepository.save(request.getRequestingUser());
        cognitoUserService.setAffiliatedArtist(sub, request.getRequestedArtist().getId());

        ArtistRequest saved = requestRepository.save(request);

        log.info("Linked userId={} to artistId={} after request with requestId={} got accepted by {}.",
                request.getRequestingUser().getId(), request.getRequestedArtist().getId(), request.getId(), admin.getDisplayName());

        return new ArtistRequestResultDTO(requestMapper.toFullResponse(saved), false);
    }

    /**
     * Calls catalog-command-service to request the creation of a new artist starting from an {@code ArtistRequest}.
     * Then saves it in the auth's projection.
     *
     * @param request The {@code ArtistRequest} to create the artist from.
     *
     * @return The auth projection of the created artist
     * @throws ConflictException if the command service returns a 409
     * @throws InternalServerErrorException if the command service returns any other status code
     */
    private ArtistAuthProjection createArtist(ArtistRequest request) {
        ArtistSummaryResponseDTO artistCreatedResponse;
        try {
            artistCreatedResponse = catalogComRestClient.post()
                    .uri(artistCreationUri)
                    .contentType(APPLICATION_JSON)
                    .body(requestMapper.toArtistCreationRequest(request))
                    .retrieve()
                    .body(ArtistSummaryResponseDTO.class);
        } catch (HttpClientErrorException.Conflict e) {
            GeneralErrorResponseDTO errorResponse = e.getResponseBodyAs(GeneralErrorResponseDTO.class);
            String upstreamMessage = errorResponse != null ? errorResponse.getMessage() : "unknown error";
            throw new ConflictException("Cannot create the artist: " + upstreamMessage);
        }  catch (HttpClientErrorException e) {
            throw new InternalServerErrorException("Unexpected error while creating the artist. Please try again later.");
        }

        ArtistAuthProjection createdArtist = artistMapper.toEntity(artistCreatedResponse);
        artistRepository.save(createdArtist);
        log.info("Created new artist (name={}, affiliatedUserId={}) after request with requestId={} got accepted.",
                request.getRequestedName(), request.getRequestingUser().getId(), request.getId());

        return createdArtist;
    }

    /**
     * Set the given request to ACCEPTED and create a new artist
     * to affiliated to the requesting user.
     *
     * @param request The request to accept
     * @param admin The admin that accepted the request - only used for logging
     *
     * @return A dto containing the accepted artist request
     */
    private ArtistRequestResultDTO acceptForNewArtist(ArtistRequest request, AppUser admin) {
        String sub = accessMethodService.getSubFromUser(request.getRequestingUser());

        request.setStatus(ArtistRequest.RequestStatus.ACCEPTED);

        ArtistAuthProjection createdArtist = createArtist(request);

        request.getRequestingUser().setAffiliatedArtist(createdArtist);
        userRepository.save(request.getRequestingUser());
        cognitoUserService.setAffiliatedArtist(sub, createdArtist.getId());

        ArtistRequest saved = requestRepository.save(request);

        log.info("Artist request with requestId={} got accepted by {} (userId={})",
                request.getId(), admin.getDisplayName(), admin.getId());

        return new ArtistRequestResultDTO(requestMapper.toFullResponse(saved), false);
    }

    // TODO Javadoc
    private ArtistRequestResultDTO accept(ArtistRequest request, AppUser admin) {
        if (request.getRequestedArtist() != null)
            return acceptForExistingArtist(request, admin);

        // At this point the request must be for a new artist
        ArtistRequest duplicate = requestRepository.findOneAcceptedByRequestedName(request.getRequestedName())
                .orElse(null);

        if (duplicate != null)
            return convertToExistingArtistRequest(request, duplicate, admin);

        return acceptForNewArtist(request, admin);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ArtistRequestResultDTO evaluateRequest(UUID requestId, boolean accepted) {
        AppUser evaluator = currentUserProvider.getCurrentUser();
        ArtistRequest artistRequest = findByIdOrThrow(requestId);

        validateIsPending(artistRequest);

        if (accepted)
            return accept(artistRequest, evaluator);

        else
            return reject(artistRequest, evaluator);
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
