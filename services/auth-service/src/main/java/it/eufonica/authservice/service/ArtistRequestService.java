package it.eufonica.authservice.service;

import it.eufonica.authservice.dto.artistrequest.*;
import it.eufonica.authservice.exception.client.BadRequestException;
import it.eufonica.authservice.exception.client.ConflictException;
import it.eufonica.authservice.exception.client.NotFoundException;
import it.eufonica.authservice.model.ArtistRequest;

import java.util.List;
import java.util.UUID;

public interface ArtistRequestService {
    /**
     * Finds an artist request by its id.
     *
     * @param id the unique id of the artist request
     * @return the artist request associated with the given id
     * @throws NotFoundException if no artist request exists with the given id
     */
    ArtistRequest findByIdOrThrow(UUID id);

    /**
     * Submits a request, on behalf of the currently authenticated user, to become
     * an artist by registering a brand new one.
     *
     * @param request the name and foundation date of the artist to create
     *
     * @return the newly created, pending artist request
     * @throws ConflictException if the user is already affiliated to an artist,
     * already has a pending request, or the foundation date is in the future
     */
    ArtistRequestFullResponseDTO sendNewArtistRequest(SendNewArtistRequestDTO request);

    /**
     * Submits a request, on behalf of the currently authenticated user, to become
     * an artist by affiliating to an already existing one.
     *
     * @param request the id of the existing artist to affiliate to
     *
     * @return the newly created, pending artist request
     * @throws NotFoundException if no artist exists with the given id
     * @throws ConflictException if the user is already affiliated to an artist,
     * already has a pending request, or the artist registered after this request
     */
    ArtistRequestFullResponseDTO sendExistingArtistRequest(SendExistingArtistRequestDTO request);

    /**
     * Evaluates a pending artist request, accepting or rejecting it.
     * <p>On acceptance, see {@code accept} for the full resolution logic, including
     * the new-artist creation and existing-request-name conflict handling.</p>
     *
     * @param requestId the id of the request to evaluate
     * @param evaluation Object with a single field "isAccepted":
     * true to accept the request, false to reject it
     *
     * @return a dto describing the outcome, including whether the original request
     * was converted into an existing-artist request due to a name conflict
     * @throws ConflictException if the request has already been evaluated
     * or if the admin tries to approve their own request
     */
    ArtistRequestResultDTO evaluateRequest(UUID requestId, EvaluateArtistRequestDTO evaluation);

    /**
     * Searches artist requests across all users, matching the given filters.
     * Restricted to admins.
     *
     * @param filters the filters to apply, including an optional requesting user id
     * @param pageNumber the one-based page number to retrieve
     * @param pageSize the number of results per page, capped at a configured maximum
     *
     * @return the matching artist requests, as short summaries
     * @throws BadRequestException if pageNumber or pageSize is less than 1
     */
    List<ArtistRequestShortResponseDTO> searchAllRequests(ArtistRequestFiltersDTO filters, int pageNumber, int pageSize);

    /**
     * Searches the artist requests submitted by the currently authenticated user,
     * matching the given filters.
     *
     * @param filters the filters to apply; the requesting user is always the current user
     * @param pageNumber the one-based page number to retrieve
     * @param pageSize the number of results per page, capped at a configured maximum
     *
     * @return the matching artist requests, as short summaries
     * @throws BadRequestException if pageNumber or pageSize is less than 1
     */
    List<ArtistRequestShortResponseDTO> searchOwnRequests(CommonArtistRequestFiltersDTO filters, int pageNumber, int pageSize);

    /**
     * Finds the full detail of a single artist request.
     * Accessible by admins, or by the user who submitted the request.
     *
     * @param requestId the id of the request to find
     *
     * @return the full detail of the request
     * @throws NotFoundException if no request exists with the given id, or if the
     * current user has no permission to view it (returned instead of a 403, to
     * avoid revealing the existence of the request)
     */
    ArtistRequestFullResponseDTO findRequest(UUID requestId);
}
