package it.musicplatform.catalogcommandservice.controller;

import it.musicplatform.catalogcommandservice.dto.song.PublishSongRequestDTO;
import it.musicplatform.catalogcommandservice.dto.song.SongResponseDTO;
import it.musicplatform.catalogcommandservice.service.SongCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public SongResponseDTO publishSong(
            @RequestPart("creationRequest") @Valid PublishSongRequestDTO creationRequestDTO,
            @PathVariable UUID artistId,
            @RequestPart("audioFile") MultipartFile audioFile
    ) {
        SongResponseDTO song = songCommandService.publishSong(creationRequestDTO, artistId, audioFile);
        log.info("Song published: {}.", song);
        return song;
    }
}
