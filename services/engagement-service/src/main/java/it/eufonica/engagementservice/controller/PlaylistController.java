package it.eufonica.engagementservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.engagementservice.dto.entry.PlaylistEntrySortOrder;
import it.eufonica.engagementservice.dto.playlist.*;
import it.eufonica.engagementservice.service.LikeService;
import it.eufonica.engagementservice.service.PlaylistCommandService;
import it.eufonica.engagementservice.service.PlaylistQueryService;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import it.eufonica.exception.dto.ValidationErrorResponseDTO;
import it.eufonica.web.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/playlists")
@RequiredArgsConstructor @Slf4j
public class PlaylistController {
    private final PlaylistCommandService commandService;
    private final PlaylistQueryService queryService;
    private final LikeService likeService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Playlist created."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing or invalid name, missing visibility).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistCreatedResponseDTO createPlaylist(@RequestBody @Valid PlaylistCreateRequestDTO request) {
        PlaylistCreatedResponseDTO playlist = commandService.createPlaylist(request);

        log.info("Playlist has been created. playlistId={}.", playlist.getCreatedPlaylistId());

        return playlist;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Playlist renamed."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing or invalid name).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not the playlist owner or an admin, and the playlist is not PRIVATE (reason: NOT_PLAYLIST_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, or is PRIVATE and the caller is not its owner (hidden from non-owners, reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PatchMapping(path = "/{playlistId}/name", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rename(@PathVariable UUID playlistId, @RequestBody @Valid PlaylistRenameRequestDTO request) {
        commandService.rename(playlistId, request.getNewName());
        log.info("Playlist has been renamed. playlistId={}, newName={}.", playlistId, request.getNewName());
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Playlist visibility changed."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing newVisibility).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not the playlist owner (reason: NOT_PLAYLIST_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, or is PRIVATE and the caller is not its owner (hidden from non-owners, reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PatchMapping(path = "/{playlistId}/visibility", consumes =  APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setVisibility(@PathVariable UUID playlistId, @RequestBody @Valid PlaylistSetVisibilityRequestDTO request) {
        commandService.setVisibility(playlistId, request.getNewVisibility());
        log.info("Playlist visibility changed. playlistId={}, newVisibility={}", playlistId, request.getNewVisibility());
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Playlist deleted."),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not the playlist owner or an admin, and the playlist is not PRIVATE (reason: NOT_PLAYLIST_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, or is PRIVATE and the caller is not its owner (hidden from non-owners, reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @DeleteMapping(path = "/{playlistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlaylist(@PathVariable UUID playlistId) {
        commandService.deletePlaylist(playlistId);
        log.info("Playlist has been deleted. playlistId={}.", playlistId);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Playlists matching the given filters, paginated."),
            @ApiResponse(responseCode = "400", description = "A filter range is invalid (negative value, or max lower than min), pageNumber or pageSize is less than 1 (reason: INVALID_PAGINATION_PARAMETER), or creator is neither \"me\" nor a valid UUID (reason: MALFORMED_REQUEST).",
                    content = @Content(schema = @Schema(oneOf = {ValidationErrorResponseDTO.class, GeneralErrorResponseDTO.class}))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "showOnlyUnlisted is true, the caller is not searching their own playlists, and the caller is not an admin (reason: NOT_AN_ADMIN).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<PlaylistShortResponseDTO> searchPlaylists(
            @ModelAttribute @Valid PlaylistSearchFiltersDTO filters,
            @RequestParam(required = false) PlaylistSortOrder sortOrder,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "${PLAYLIST_PAGE_DEFAULT_SIZE:50}") Integer pageSize
    ) {
        PageResponseDTO<PlaylistShortResponseDTO> result = queryService.searchPlaylists(filters, sortOrder, pageNumber, pageSize);
        log.debug("Songs searched: found {} results (page {}/{}).", result.getTotalElements(), pageNumber, result.getTotalPages());
        return result;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Playlist found."),
            @ApiResponse(responseCode = "400", description = "pageNumber or pageSize is less than 1 (reason: INVALID_PAGINATION_PARAMETER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, or is PRIVATE and the caller is not its owner (hidden from non-owners, reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @GetMapping(path = "/{playlistId}", produces = APPLICATION_JSON_VALUE)
    public PlaylistResponseDTO findPlaylist(
            @PathVariable UUID playlistId,
            @RequestParam(required = false) PlaylistEntrySortOrder sortOrder,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "${PLAYLIST_ENTRY_PAGE_DEFAULT_SIZE:50}") Integer pageSize
    ) {
        PlaylistResponseDTO result = queryService.findPlaylist(playlistId, sortOrder, pageNumber, pageSize);
        log.debug("Playlist found: id={}, name={}, numberOfEntries={} (entry page {}/{})",
                playlistId, result.getName(), result.getPlaylistEntries().getTotalElements(),
                pageNumber, pageSize
        );
        return result;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Playlist liked."),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, or is PRIVATE and the caller is not its owner (hidden from non-owners, reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PutMapping(path = "/{playlistId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void like(@PathVariable UUID playlistId) {
        likeService.likePlaylist(playlistId);
        log.debug("Liking playlist with id={}.", playlistId);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Playlist unliked."),
            @ApiResponse(responseCode = "401", description = "No authenticated subject could be identified (reason: UNAUTHENTICATED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The playlist does not exist, or is PRIVATE and the caller is not its owner (hidden from non-owners, reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @DeleteMapping(path = "{playlistId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlike(@PathVariable UUID playlistId) {
        likeService.unlikePlaylist(playlistId);
        log.debug("Unliking playlist with id={}.", playlistId);
    }
}
