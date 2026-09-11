package it.eufonica.catalogcommandservice.controller.external;

import it.eufonica.catalogcommandservice.dto.song.PublishSongRequestDTO;
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

    @PostMapping(path = "/{artistId}",
            consumes = MULTIPART_FORM_DATA_VALUE,
            produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SongResponseDTO publishSong(
            @RequestPart("creationRequest") @Valid PublishSongRequestDTO creationRequestDTO,
            @PathVariable UUID artistId,
            @RequestPart("audioFile") MultipartFile audioFile
    ) {
        SongResponseDTO song = songCommandService.publishSong(creationRequestDTO, artistId, audioFile);
        log.info("Song published: {}.", song);
        return song;
    }

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
    public SongResponseDTO removeCreditedArtist(
            @PathVariable UUID idSong,
            @PathVariable UUID idArtist
    ) {
        SongResponseDTO response = songCommandService.removeCreditedArtist(idSong, idArtist);
        log.info("Removed credited artist {} from song {}.", idArtist, idSong);
        return response;
    }
}
