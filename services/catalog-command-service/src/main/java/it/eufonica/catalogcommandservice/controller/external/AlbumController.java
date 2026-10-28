package it.eufonica.catalogcommandservice.controller.external;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.catalogcommandservice.dto.album.AlbumCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumSummaryResponseDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumUpdateRequestDTO;
import it.eufonica.catalogcommandservice.service.core.AlbumCommandService;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import it.eufonica.exception.dto.ValidationErrorResponseDTO;
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
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing name, more than 2 featured artists, no songs).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER (reason: NOT_AN_USER) or has no artist affiliation (reason: NOT_AN_ARTIST).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "A featured artist or a song referenced in the request does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "An artist/album temporal constraint is violated (reason: ARTIST_FOUNDED_AFTER_ALBUM_RELEASE, ARTIST_REGISTERED_AFTER_ALBUM_PUBLICATION or ARTIST_FOUNDED_AFTER_ALBUM_PUBLICATION) or no credited artist is common to every song in the album (reason: ALBUM_MISSING_COMMON_CREDITED_ARTIST).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
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
            @ApiResponse(responseCode = "400", description = "Request validation failed (e.g. missing name, no songs).",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER (reason: NOT_AN_USER), has no artist affiliation (reason: NOT_AN_ARTIST) or is not credited on this album (reason: NOT_RESOURCE_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The album does not exist or a song referenced in the request does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Original release date is after the album's publication date (reason: ALBUM_RELEASE_DATE_AFTER_PUBLICATION_DATE) or no credited artist is common to every song in the album (reason: ALBUM_MISSING_COMMON_CREDITED_ARTIST).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PutMapping(path = "/{albumId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public AlbumSummaryResponseDTO updateAlbum(@PathVariable UUID albumId, @RequestBody @Valid AlbumUpdateRequestDTO requestDTO) {
        AlbumSummaryResponseDTO album = albumCommandService.updateAlbum(albumId, requestDTO);
        log.info("Album updated: {}.", album);
        return album;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Album deleted."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER (reason: NOT_AN_USER), or caller is not an admin and either has no artist affiliation (reason: NOT_AN_ARTIST) or is not credited on this album (reason: NOT_RESOURCE_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The album does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @DeleteMapping(path = "/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable UUID albumId) {
        albumCommandService.deleteAlbum(albumId);
        log.info("Album deleted: {}.", albumId);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song added to the album."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER (reason: NOT_AN_USER), has no artist affiliation (reason: NOT_AN_ARTIST) or is not credited on this album (reason: NOT_RESOURCE_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The album does not exist or the song does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "No credited artist is common to every song in the album after the addition (reason: ALBUM_MISSING_COMMON_CREDITED_ARTIST).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @PutMapping(path = "/{albumId}/songs/{idSong}")
    public AlbumSummaryResponseDTO addSongToAlbum(@PathVariable UUID albumId, @PathVariable UUID idSong) {
        AlbumSummaryResponseDTO album = albumCommandService.addSongToAlbum(albumId, idSong);
        log.info("Song added to album. Updated album: {}.", album);
        return album;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song removed from the album."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER (reason: NOT_AN_USER), has no artist affiliation (reason: NOT_AN_ARTIST) or is not credited on this album (reason: NOT_RESOURCE_OWNER).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "The album does not exist (reason: RESOURCE_NOT_FOUND) or the song is not part of it (reason: SONG_NOT_IN_ALBUM).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Removing the song would leave the album empty (reason: ALBUM_EMPTY_AFTER_SONG_REMOVED).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @DeleteMapping(path = "/{albumId}/songs/{idSong}")
    public AlbumSummaryResponseDTO removeSongFromAlbum(@PathVariable UUID albumId, @PathVariable UUID idSong) {
        AlbumSummaryResponseDTO album = albumCommandService.removeSongFromAlbum(albumId, idSong, true); // TODO Placeholder hardcoded "true" value for confirmCascade
        log.info("Song removed from album. Updated album: {}.", album);
        return album;
    }
}
