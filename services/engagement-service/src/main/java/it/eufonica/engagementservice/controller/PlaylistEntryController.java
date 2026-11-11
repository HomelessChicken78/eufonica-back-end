package it.eufonica.engagementservice.controller;

import it.eufonica.engagementservice.dto.entry.*;
import it.eufonica.engagementservice.service.PlaylistEntryService;
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

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public List<PlaylistEntryShortResponseDTO> addEntryToSong(@PathVariable UUID playlistId, @RequestBody @Valid PlaylistEntryCreateRequestDTO request) {
        return entryService.addSongsToPlaylist(playlistId, request.getSongIds());
    }

    @DeleteMapping(path = "/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable UUID playlistId, @PathVariable UUID entryId) {
        entryService.deletePlaylistEntry(playlistId, entryId);
        log.info("Removed entry from playlist. (songId={}, playlistId={})", entryId, playlistId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bulkDeleteEntries(@PathVariable UUID playlistId, @RequestBody @Valid PlaylistEntryBulkDeleteRequestDTO request) {
        entryService.bulkDeleteEntries(playlistId, request.getEntryIds());
        log.info("Removed entry from playlist. (deleted {} entries, playlistId={})", request.getEntryIds().size(), playlistId);
    }

    @PatchMapping(path = "/{entryId}/rank")
    public PlaylistEntryRepositionResponseDTO repositionEntry(
            @PathVariable UUID playlistId, @PathVariable UUID entryId, @RequestBody @Valid PlaylistEntryRepositionRequestDTO request
    ) {
        return entryService.reposition(playlistId, entryId, request);
    }
}
