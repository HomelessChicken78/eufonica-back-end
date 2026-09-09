package it.eufonica.catalogcommandservice.controller;

import it.eufonica.catalogcommandservice.dto.album.AlbumCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumSummaryResponseDTO;
import it.eufonica.catalogcommandservice.dto.artist.*;
import it.eufonica.catalogcommandservice.service.AlbumCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/albums")
@RequiredArgsConstructor @Slf4j
public class AlbumController {
    private final AlbumCommandService albumCommandService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumSummaryResponseDTO createAlbum(@RequestBody @Valid AlbumCreationRequestDTO creationRequestDTO) {
        AlbumSummaryResponseDTO album = albumCommandService.createAlbum(creationRequestDTO);
        log.info("Album created: {}.", album);
        return album;
    }

    @PutMapping(path = "/{albumId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public AlbumSummaryResponseDTO updateAlbum(@PathVariable UUID albumId, @RequestBody @Valid AlbumCreationRequestDTO requestDTO) {
        AlbumSummaryResponseDTO album = albumCommandService.updateAlbum(albumId, requestDTO);
        log.info("Album updated: {}.", album);
        return album;
    }

    @DeleteMapping(path = "/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable UUID albumId) {
        albumCommandService.deleteAlbum(albumId);
        log.info("Album deleted: {}.", albumId);
    }

    @PutMapping(path = "/{albumId}/songs/{idSong}")
    public AlbumSummaryResponseDTO addSongToAlbum(@PathVariable UUID albumId, @PathVariable UUID idSong) {
        AlbumSummaryResponseDTO album = albumCommandService.addSongToAlbum(albumId, idSong);
        log.info("Song added to album. Updated album: {}.", album);
        return album;
    }

    @DeleteMapping(path = "/{albumId}/songs/{idSong}")
    public AlbumSummaryResponseDTO removeSongFromAlbum(@PathVariable UUID albumId, @PathVariable UUID idSong) {
        AlbumSummaryResponseDTO album = albumCommandService.removeSongFromAlbum(albumId, idSong);
        log.info("Song removed from album. Updated album: {}.", album);
        return album;
    }
}
