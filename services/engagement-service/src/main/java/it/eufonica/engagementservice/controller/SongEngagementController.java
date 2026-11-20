package it.eufonica.engagementservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.engagementservice.dto.listen.ListenCreateRequestDTO;
import it.eufonica.engagementservice.service.LikeService;
import it.eufonica.engagementservice.service.ListenService;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import it.eufonica.exception.dto.ValidationErrorResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/songs/{songId}")
@RequiredArgsConstructor @Slf4j
public class SongEngagementController {
    private final LikeService likeService;
    private final ListenService listenService;

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Song liked."),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The song does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PutMapping(path = "/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void like(@PathVariable UUID songId) {
        likeService.likeSong(songId);
        log.debug("Liking song with id={}.", songId);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Song unliked."),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The song does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @DeleteMapping(path = "/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlike(@PathVariable UUID songId) {
        likeService.unlikeSong(songId);
        log.debug("Unliking song with id={}.", songId);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Listen registered, or silently ignored as a no-op if secPlayed does not meet the minimum play threshold for the song, or the caller listened to the song too recently (cooldown)."),
            @ApiResponse(responseCode = "400", description = "secPlayed is missing or negative (reason: VALIDATION_ERROR), or is greater than the song's duration (reason: INVALID_SEC_PLAYED).",
                    content = @Content(schema = @Schema(oneOf = {ValidationErrorResponseDTO.class, GeneralErrorResponseDTO.class}))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The song does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PostMapping(path = "listens", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void listen(@PathVariable UUID songId, @RequestBody @Valid ListenCreateRequestDTO request) {
        listenService.listen(songId, request.getSecPlayed());
    }
}
