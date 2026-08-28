package it.musicplatform.catalogcommandservice.controller;

import it.musicplatform.catalogcommandservice.dto.artist.*;
import it.musicplatform.catalogcommandservice.service.ArtistCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/internal/artists")
@RequiredArgsConstructor
public class ArtistInternalController {
    private final ArtistCommandService artistCommandService;

    // TODO This endpoint should be protected since it's an internal endpoint
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistSummaryResponseDTO createArtist(@RequestBody @Valid ArtistCreationRequestDTO creationRequestDTO) {
        return artistCommandService.createArtist(creationRequestDTO);
    }
}
