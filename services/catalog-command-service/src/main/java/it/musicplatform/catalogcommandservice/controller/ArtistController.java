package it.musicplatform.catalogcommandservice.controller;

import it.musicplatform.catalogcommandservice.dto.artist.*;
import it.musicplatform.catalogcommandservice.service.ArtistCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/artists")
@RequiredArgsConstructor
public class ArtistController {
    private final ArtistCommandService artistCommandService;

    @PutMapping(path = "/{artistId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistSummaryResponseDTO updateArtist(@PathVariable UUID artistId,
            @RequestBody @Valid ArtistCreationRequestDTO creationRequestDTO) {
        return artistCommandService.updateArtist(artistId, creationRequestDTO);
    }
}
