package it.eufonica.authservice.controller;

import it.eufonica.authservice.dto.artistrequest.*;
import it.eufonica.authservice.security.CurrentUserProvider;
import it.eufonica.authservice.service.ArtistRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/artists/requests")
@RequiredArgsConstructor @Slf4j
public class ArtistRequestController {
    private final ArtistRequestService requestService;

    @PostMapping(value = "/new-artist",
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistRequestFullResponseDTO sendNewArtistRequest(@RequestBody @Valid SendNewArtistRequestDTO request) {
        ArtistRequestFullResponseDTO response = requestService.sendNewArtistRequest(request);
        log.info("Sent new-artist request with requestedName={}. Created requestId={}.", request.getRequestedName(), response.getId());
        return response;
    }

    @PostMapping(value = "/existing-artist",
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistRequestFullResponseDTO sendExistingArtistRequest(@RequestBody @Valid SendExistingArtistRequestDTO request) {
        ArtistRequestFullResponseDTO response = requestService.sendExistingArtistRequest(request);
        log.info("Sent existing-artist request with artistId={}. Created requestId={}.", request.getArtistId(), response.getId());
        return response;
    }

    @PatchMapping(value = "/{requestId}/evaluate", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistRequestResultDTO evaluateRequest(@PathVariable UUID requestId, @RequestBody EvaluateArtistRequestDTO evaluation) {
        ArtistRequestResultDTO result = requestService.evaluateRequest(requestId, evaluation);
        log.debug("{} request with id {}. result {}", evaluation.isAccepted() ? "Accepted" : "Rejected", requestId, result);
        return result;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<ArtistRequestShortResponseDTO> searchAllRequests(@ModelAttribute @Valid ArtistRequestFiltersDTO filters,
                                                                 @RequestParam(defaultValue = "1") int pageNumber,
                                                                 @RequestParam(defaultValue = "${REQUEST_PAGE_DEFAULT_SIZE:10}") int pageSize) {
        List<ArtistRequestShortResponseDTO> requests = requestService.searchAllRequests(filters, pageNumber, pageSize);
        log.debug("Searched all requests with filters {}. Found {} results.", filters, requests.size());
        return requests;
    }

    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    public List<ArtistRequestShortResponseDTO> searchOwnRequests(@ModelAttribute @Valid CommonArtistRequestFiltersDTO filters,
                                                                 @RequestParam(defaultValue = "1") int pageNumber,
                                                                 @RequestParam(defaultValue = "${REQUEST_PAGE_DEFAULT_SIZE:10}") int pageSize) {
        List<ArtistRequestShortResponseDTO> requests = requestService.searchOwnRequests(filters, pageNumber, pageSize);
        log.debug("Searched own requests with filters {}. Found {} results.", filters, requests.size());
        return requests;
    }

    @GetMapping(value = "/{requestId}", produces = APPLICATION_JSON_VALUE)
    public ArtistRequestFullResponseDTO findRequest(@PathVariable UUID requestId) {
        ArtistRequestFullResponseDTO found = requestService.findRequest(requestId);
        log.debug("Found artist request with id {}.", requestId);
        return found;
    }
}
