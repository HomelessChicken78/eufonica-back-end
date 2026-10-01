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

    @PatchMapping(value = "/{requestId}/evaluate", consumes =  APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistRequestResultDTO evaluateRequest(@PathVariable UUID requestId, @RequestBody EvaluateArtistRequestDTO evaluation) {
        ArtistRequestResultDTO result = requestService.evaluateRequest(requestId, evaluation);
        log.debug("{} request with id {}. result {}", evaluation.isAccepted() ? "Accepted" : "Rejected", requestId, result);
        return result;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<ArtistRequestShortResponseDTO> searchAllRequests(@ModelAttribute @Valid ArtistRequestFiltersDTO filters,
                                                          int pageNumber, int pageSize) {
        List<ArtistRequestShortResponseDTO> requests = requestService.searchAllRequests(filters, pageNumber, pageSize);
        log.debug("Searched all requests with filters {}. Found {} results.", filters, requests.size());
        return requests;
    }

    @GetMapping(value = "/me", produces = APPLICATION_JSON_VALUE)
    public List<ArtistRequestShortResponseDTO> searchOwnRequests(@ModelAttribute @Valid CommonArtistRequestFiltersDTO filters,
                                                          int pageNumber, int pageSize) {
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
