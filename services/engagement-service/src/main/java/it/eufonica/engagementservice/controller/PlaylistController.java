package it.eufonica.engagementservice.controller;

import it.eufonica.engagementservice.dto.playlist.PlaylistCreateRequestDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistCreatedResponseDTO;
import it.eufonica.engagementservice.service.PlaylistCommandService;
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

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistCreatedResponseDTO createPlaylist(@RequestBody @Valid PlaylistCreateRequestDTO request) {
        PlaylistCreatedResponseDTO playlist = commandService.createPlaylist(request);

        log.info("Public playlist has been created. playlistId={}.", playlist.getCreatedPlaylistId());

        return playlist;
    }
}
