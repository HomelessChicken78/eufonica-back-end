package it.eufonica.authservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.authservice.dto.artistrequest.*;
import it.eufonica.authservice.dto.common.PageResponseDTO;
import it.eufonica.authservice.service.ArtistRequestService;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import it.eufonica.exception.dto.ValidationErrorResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/artists/requests")
@RequiredArgsConstructor @Slf4j
public class ArtistRequestController {
    private final ArtistRequestService requestService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New-artist request created."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing requested name or foundation date).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER (reason: NOT_AN_USER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Caller is already an artist (reason: ALREADY_AN_ARTIST) or already has a pending request (reason: ARTIST_REQUEST_ALREADY_PENDING).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PostMapping(value = "/new-artist",
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistRequestFullResponseDTO sendNewArtistRequest(@RequestBody @Valid SendNewArtistRequestDTO request) {
        ArtistRequestFullResponseDTO response = requestService.sendNewArtistRequest(request);
        log.info("Sent new-artist request with requestedName={}. Created requestId={}.", request.getRequestedName(), response.getId());
        return response;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Existing-artist request created."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing artist id).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER (reason: NOT_AN_USER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The referenced artist does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Caller is already an artist (reason: ALREADY_AN_ARTIST), already has a pending request (reason: ARTIST_REQUEST_ALREADY_PENDING) or the artist registered after the request (reason: ARTIST_REGISTERED_AFTER_REQUEST).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PostMapping(value = "/existing-artist",
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistRequestFullResponseDTO sendExistingArtistRequest(@RequestBody @Valid SendExistingArtistRequestDTO request) {
        ArtistRequestFullResponseDTO response = requestService.sendExistingArtistRequest(request);
        log.info("Sent existing-artist request with artistId={}. Created requestId={}.", request.getArtistId(), response.getId());
        return response;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request evaluated."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (missing accepted flag), or catalog-command-service rejected the artist creation as invalid.",
                    content = @Content(schema = @Schema(oneOf = {ValidationErrorResponseDTO.class, GeneralErrorResponseDTO.class}))),
            @ApiResponse(responseCode = "403", description = "Caller is not an admin (reason: NOT_AN_ADMIN).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The request does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "The request was already evaluated (reason: REQUEST_ALREADY_EVALUATED) or the admin is trying to evaluate their own request (reason: SELF_EVALUATION_NOT_ALLOWED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PatchMapping(value = "/{requestId}/evaluate", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistRequestResultDTO evaluateRequest(@PathVariable UUID requestId, @RequestBody @Valid EvaluateArtistRequestDTO evaluation) {
        ArtistRequestResultDTO result = requestService.evaluateRequest(requestId, evaluation);
        log.debug("{} request with id {}. result {}", evaluation.getAccepted() ? "Accepted" : "Rejected", requestId, result);
        return result;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests matching the given filters, paginated."),
            @ApiResponse(responseCode = "400", description = "pageNumber or pageSize is less than 1 (reason: INVALID_PAGINATION_PARAMETER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not an admin (reason: NOT_AN_ADMIN).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<ArtistRequestShortResponseDTO> searchAllRequests(@ModelAttribute @Valid CommonArtistRequestFiltersDTO commonFilters,
                                                                            @RequestParam(required = false) UUID requestingUserId,
                                                                            @RequestParam(defaultValue = "1") int pageNumber,
                                                                            @RequestParam(defaultValue = "${REQUEST_PAGE_DEFAULT_SIZE:10}") int pageSize) {
        ArtistRequestFiltersDTO filters = ArtistRequestFiltersDTO.builder()
                .commonFilters(commonFilters)
                .requestingUserId(requestingUserId)
                .build();

        return requestService.searchAllRequests(filters, pageNumber, pageSize);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The caller's own requests matching the given filters, paginated."),
            @ApiResponse(responseCode = "400", description = "pageNumber or pageSize is less than 1 (reason: INVALID_PAGINATION_PARAMETER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER (reason: NOT_AN_USER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<ArtistRequestShortResponseDTO> searchOwnRequests(@ModelAttribute @Valid CommonArtistRequestFiltersDTO filters,
                                                                 @RequestParam(defaultValue = "1") int pageNumber,
                                                                 @RequestParam(defaultValue = "${REQUEST_PAGE_DEFAULT_SIZE:10}") int pageSize) {
        PageResponseDTO<ArtistRequestShortResponseDTO> requests = requestService.searchOwnRequests(filters, pageNumber, pageSize);
        log.debug("Searched own requests with filters {}. Found {} results.", filters, requests.getContent().size());
        return requests;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request found."),
            @ApiResponse(responseCode = "404", description = "The request does not exist, or the caller is neither its owner nor an admin (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @GetMapping(value = "/{requestId}", produces = APPLICATION_JSON_VALUE)
    public ArtistRequestFullResponseDTO findRequest(@PathVariable UUID requestId) {
        ArtistRequestFullResponseDTO found = requestService.findRequest(requestId);
        log.debug("Found artist request with id {}.", requestId);
        return found;
    }
}
