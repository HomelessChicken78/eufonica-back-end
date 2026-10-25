package it.eufonica.authservice.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.authservice.dto.artistrequest.*;
import it.eufonica.authservice.dto.common.PageResponseDTO;
import it.eufonica.authservice.service.ArtistRequestService;
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
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing requested name or foundation date)."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER."),
            @ApiResponse(responseCode = "409", description = "Caller is already an artist or already has a pending request.")
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
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing artist id)."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER."),
            @ApiResponse(responseCode = "404", description = "The referenced artist does not exist."),
            @ApiResponse(responseCode = "409", description = "Caller is already an artist, already has a pending request or the artist registered after the request.")
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
            @ApiResponse(responseCode = "400", description = "Request validation failed or catalog-command-service rejected the artist creation as invalid."),
            @ApiResponse(responseCode = "403", description = "Caller is not an admin."),
            @ApiResponse(responseCode = "404", description = "The request does not exist."),
            @ApiResponse(responseCode = "409", description = "The request was already evaluated, the admin is trying to evaluate their own request or catalog-command-service rejected the artist creation as a conflict.")
    })
    @PatchMapping(value = "/{requestId}/evaluate", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistRequestResultDTO evaluateRequest(@PathVariable UUID requestId, @RequestBody @Valid EvaluateArtistRequestDTO evaluation) {
        ArtistRequestResultDTO result = requestService.evaluateRequest(requestId, evaluation);
        log.debug("{} request with id {}. result {}", evaluation.getAccepted() ? "Accepted" : "Rejected", requestId, result);
        return result;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests matching the given filters, paginated."),
            @ApiResponse(responseCode = "400", description = "pageNumber or pageSize is less than 1."),
            @ApiResponse(responseCode = "403", description = "Caller is not an admin.")
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
            @ApiResponse(responseCode = "400", description = "pageNumber or pageSize is less than 1."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER.")
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
            @ApiResponse(responseCode = "404", description = "The request does not exist, or the caller is neither its owner nor an admin.")
    })
    @GetMapping(value = "/{requestId}", produces = APPLICATION_JSON_VALUE)
    public ArtistRequestFullResponseDTO findRequest(@PathVariable UUID requestId) {
        ArtistRequestFullResponseDTO found = requestService.findRequest(requestId);
        log.debug("Found artist request with id {}.", requestId);
        return found;
    }
}
