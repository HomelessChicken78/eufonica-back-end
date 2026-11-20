package it.eufonica.engagementservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.engagementservice.dto.entry.*;
import it.eufonica.engagementservice.service.PlaylistEntryService;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import it.eufonica.exception.dto.ValidationErrorResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/playlists/{playlistId}/entries")
@RequiredArgsConstructor @Slf4j
public class PlaylistEntryController {
    private final PlaylistEntryService entryService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Songs added to the playlist."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing or empty songIds).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not the playlist owner (reason: NOT_PLAYLIST_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, is PRIVATE and the caller is not its owner (hidden from non-owners), or one of the given songIds does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<PlaylistEntryShortResponseDTO> addEntryToSong(@PathVariable UUID playlistId, @RequestBody @Valid PlaylistEntryCreateRequestDTO request) {
        return entryService.addSongsToPlaylist(playlistId, request.getSongIds());
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entry removed from the playlist."),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not the playlist owner (reason: NOT_PLAYLIST_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, is PRIVATE and the caller is not its owner (hidden from non-owners), or the entry does not exist inside the playlist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @DeleteMapping(path = "/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable UUID playlistId, @PathVariable UUID entryId) {
        entryService.deletePlaylistEntry(playlistId, entryId);
        log.info("Removed entry from playlist. (songId={}, playlistId={})", entryId, playlistId);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entries removed from the playlist."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing or empty entryIds).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not the playlist owner (reason: NOT_PLAYLIST_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, is PRIVATE and the caller is not its owner (hidden from non-owners), or one of the given entryIds does not exist inside the playlist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bulkDeleteEntries(@PathVariable UUID playlistId, @RequestBody @Valid PlaylistEntryBulkDeleteRequestDTO request) {
        entryService.bulkDeleteEntries(playlistId, request.getEntryIds());
        log.info("Removed entry from playlist. (deleted {} entries, playlistId={})", request.getEntryIds().size(), playlistId);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entry repositioned, with any cascade repositioning of other entries."),
            @ApiResponse(responseCode = "400", description = "newRankOrder is not positive (reason: VALIDATION_ERROR), or is greater than the number of entries in the playlist (reason: INVALID_RANK_ORDER).",
                    content = @Content(schema = @Schema(oneOf = {ValidationErrorResponseDTO.class, GeneralErrorResponseDTO.class}))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not the playlist owner (reason: NOT_PLAYLIST_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, is PRIVATE and the caller is not its owner (hidden from non-owners), or the entry does not exist inside the playlist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PatchMapping(path = "/{entryId}/rank")
    public PlaylistEntryRepositionResponseDTO repositionEntry(
            @PathVariable UUID playlistId, @PathVariable UUID entryId, @RequestBody @Valid PlaylistEntryRepositionRequestDTO request
    ) {
        return entryService.reposition(playlistId, entryId, request);
    }
}
