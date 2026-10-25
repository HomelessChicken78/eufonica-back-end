package it.eufonica.catalogcommandservice.controller.external;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.catalogcommandservice.dto.album.AlbumCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumSummaryResponseDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumUpdateRequestDTO;
import it.eufonica.catalogcommandservice.service.core.AlbumCommandService;
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

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Album created."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing name, more than 2 featured artists, no songs)."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER or has no artist affiliation."),
            @ApiResponse(responseCode = "404", description = "A featured artist or a song referenced in the request does not exist."),
            @ApiResponse(responseCode = "409", description = "An artist/album temporal constraint is violated or no credited artist is common to every song in the album.")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumSummaryResponseDTO createAlbum(@RequestBody @Valid AlbumCreationRequestDTO creationRequestDTO) {
        AlbumSummaryResponseDTO album = albumCommandService.createAlbum(creationRequestDTO);
        log.info("Album created: {}.", album);
        return album;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album updated."),
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing name, no songs)."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER, has no artist affiliation or is not credited on this album."),
            @ApiResponse(responseCode = "404", description = "The album does not exist or a song referenced in the request does not exist."),
            @ApiResponse(responseCode = "409", description = "Original release date is after the album's publication date or no credited artist is common to every song in the album.")
    })
    @PutMapping(path = "/{albumId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public AlbumSummaryResponseDTO updateAlbum(@PathVariable UUID albumId, @RequestBody @Valid AlbumUpdateRequestDTO requestDTO) {
        AlbumSummaryResponseDTO album = albumCommandService.updateAlbum(albumId, requestDTO);
        log.info("Album updated: {}.", album);
        return album;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Album deleted."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER, has no artist affiliation or is not credited on this album."),
            @ApiResponse(responseCode = "404", description = "The album does not exist.")
    })
    @DeleteMapping(path = "/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable UUID albumId) {
        albumCommandService.deleteAlbum(albumId);
        log.info("Album deleted: {}.", albumId);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song added to the album."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER, has no artist affiliation or is not credited on this album."),
            @ApiResponse(responseCode = "404", description = "The album does not exist or the song does not exist."),
            @ApiResponse(responseCode = "409", description = "No credited artist is common to every song in the album after the addition.")
    })
    @PutMapping(path = "/{albumId}/songs/{idSong}")
    public AlbumSummaryResponseDTO addSongToAlbum(@PathVariable UUID albumId, @PathVariable UUID idSong) {
        AlbumSummaryResponseDTO album = albumCommandService.addSongToAlbum(albumId, idSong);
        log.info("Song added to album. Updated album: {}.", album);
        return album;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song removed from the album."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER, has no artist affiliation or is not credited on this album."),
            @ApiResponse(responseCode = "404", description = "The album does not exist or the song is not part of it."),
            @ApiResponse(responseCode = "409", description = "Removing the song would leave the album empty.")
    })
    @DeleteMapping(path = "/{albumId}/songs/{idSong}")
    public AlbumSummaryResponseDTO removeSongFromAlbum(@PathVariable UUID albumId, @PathVariable UUID idSong) {
        AlbumSummaryResponseDTO album = albumCommandService.removeSongFromAlbum(albumId, idSong);
        log.info("Song removed from album. Updated album: {}.", album);
        return album;
    }
}
