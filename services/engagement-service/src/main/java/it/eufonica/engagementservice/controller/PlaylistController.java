package it.eufonica.engagementservice.controller;

import it.eufonica.engagementservice.dto.entry.PlaylistEntrySortOrder;
import it.eufonica.engagementservice.dto.playlist.*;
import it.eufonica.engagementservice.service.PlaylistCommandService;
import it.eufonica.engagementservice.service.PlaylistQueryService;
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

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistCreatedResponseDTO createPlaylist(@RequestBody @Valid PlaylistCreateRequestDTO request) {
        PlaylistCreatedResponseDTO playlist = commandService.createPlaylist(request);

        log.info("Playlist has been created. playlistId={}.", playlist.getCreatedPlaylistId());

        return playlist;
    }

    @PatchMapping(path = "/{playlistId}/name", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rename(@PathVariable UUID playlistId, @RequestBody @Valid PlaylistRenameRequestDTO request) {
        commandService.rename(playlistId, request.getNewName());
        log.info("Playlist has been renamed. playlistId={}, newName={}.", playlistId, request.getNewName());
    }

    @PatchMapping(path = "/{playlistId}/visibility", consumes =  APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setVisibility(@PathVariable UUID playlistId, @RequestBody @Valid PlaylistSetVisibilityRequestDTO request) {
        commandService.setVisibility(playlistId, request.getNewVisibility());
        log.info("Playlist visibility changed. playlistId={}, newVisibility={}", playlistId, request.getNewVisibility());
    }

    @DeleteMapping(path = "/{playlistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlaylist(@PathVariable UUID playlistId) {
        commandService.deletePlaylist(playlistId);
        log.info("Playlist has been deleted. playlistId={}.", playlistId);
    }

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

    @GetMapping(path = "/{playlistId}", produces = APPLICATION_JSON_VALUE)
    public PlaylistResponseDTO searchPlaylists(
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
}
