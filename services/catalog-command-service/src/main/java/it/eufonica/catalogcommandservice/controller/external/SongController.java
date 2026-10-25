package it.eufonica.catalogcommandservice.controller.external;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.catalogcommandservice.dto.song.PublishSongRequestDTO;
import it.eufonica.catalogcommandservice.dto.song.SongDeletionResultDTO;
import it.eufonica.catalogcommandservice.dto.song.SongResponseDTO;
import it.eufonica.catalogcommandservice.service.core.SongCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController @RequestMapping("/songs")
@RequiredArgsConstructor @Slf4j
public class SongController {
    private final SongCommandService songCommandService;

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Song published."),
            @ApiResponse(responseCode = "400", description = "Request validation failed or the audio file is missing, empty or has an unsupported MIME type."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER or has no artist affiliation."),
            @ApiResponse(responseCode = "404", description = "A credited artist referenced in the request does not exist."),
            @ApiResponse(responseCode = "409", description = "A song with this title already exists for this artist or a credited artist's foundation date is after the song's published date."),
            @ApiResponse(responseCode = "413", description = "The audio file exceeds the maximum allowed size.")
    })
    public SongResponseDTO publishSong(
            @RequestPart("creationRequest") @Valid PublishSongRequestDTO creationRequestDTO,
            @RequestPart("audioFile") MultipartFile audioFile
    ) {
        SongResponseDTO song = songCommandService.publishSong(creationRequestDTO, audioFile);
        log.info("Song published: {}.", song);
        return song;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song deleted."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER or is not the song's owning artist."),
            @ApiResponse(responseCode = "404", description = "The song does not exist."),
            @ApiResponse(responseCode = "409", description = "The deletion would leave one or more albums empty and confirmCascade was not set to true.")
    })
    @DeleteMapping(path = "/{idSong}", produces = APPLICATION_JSON_VALUE)
    public SongDeletionResultDTO deleteSong(
            @PathVariable UUID idSong,
            @RequestParam(defaultValue = "false") boolean confirmCascade
    ) {
        SongDeletionResultDTO response = songCommandService.deleteSong(idSong, confirmCascade);
        log.info("Deleted song {}.", idSong);
        return response;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artist credited on the song."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER or is not the song's owning artist."),
            @ApiResponse(responseCode = "404", description = "The song does not exist or the artist does not exist.")
    })
    @PutMapping(path = "/{idSong}/artists/{idArtist}", produces = APPLICATION_JSON_VALUE)
    public SongResponseDTO addCreditedArtist(
            @PathVariable UUID idSong,
            @PathVariable UUID idArtist
    ) {
        SongResponseDTO response = songCommandService.addCreditedArtist(idSong, idArtist);
        log.info("Added credited artist {} to song {}.", idArtist, idSong);
        return response;
    }

    @DeleteMapping(path = "/{idSong}/artists/{idArtist}", produces = APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artist removed from the song's credits."),
            @ApiResponse(responseCode = "403", description = "Caller is not a USER or is not the song's owning artist."),
            @ApiResponse(responseCode = "404", description = "The song does not exist or the artist does not exist."),
            @ApiResponse(responseCode = "409", description = "The artist is the song's primary owner and cannot be removed from its credits or removing it would leave an album without a common credited artist.")
    })
    public SongResponseDTO removeCreditedArtist(
            @PathVariable UUID idSong,
            @PathVariable UUID idArtist
    ) {
        SongResponseDTO response = songCommandService.removeCreditedArtist(idSong, idArtist);
        log.info("Removed credited artist {} from song {}.", idArtist, idSong);
        return response;
    }
}
