package it.eufonica.engagementservice.controller;

import it.eufonica.engagementservice.dto.playlist.*;
import it.eufonica.engagementservice.service.PlaylistCommandService;
import it.eufonica.engagementservice.service.PlaylistQueryService;
import it.eufonica.web.dto.PageResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

        log.info("Public playlist has been created. playlistId={}.", playlist.getCreatedPlaylistId());

        return playlist;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<PlaylistShortResponseDTO> searchPlaylists(
            @ModelAttribute @Valid PlaylistSearchFiltersDTO filters,
            @RequestParam(required = false) PlaylistResponseSortOrder sortOrder,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "${SONG_PAGE_DEFAULT_SIZE:50}") Integer pageSize
    ) {
        PageResponseDTO<PlaylistShortResponseDTO> result = queryService.searchPlaylists(filters, sortOrder, pageNumber, pageSize);
        log.debug("Songs searched: found {} results (page {}/{}).", result.getTotalElements(), pageNumber, result.getTotalPages());
        return result;
    }
}
