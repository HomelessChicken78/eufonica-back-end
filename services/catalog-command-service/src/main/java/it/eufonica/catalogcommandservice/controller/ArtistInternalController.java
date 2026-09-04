package it.eufonica.catalogcommandservice.controller;

import it.eufonica.catalogcommandservice.dto.artist.ArtistCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.artist.ArtistSummaryResponseDTO;
import it.eufonica.catalogcommandservice.dto.artist.*;
import it.eufonica.catalogcommandservice.service.ArtistCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/internal/artists")
@RequiredArgsConstructor @Slf4j
public class ArtistInternalController {
    private final ArtistCommandService artistCommandService;

    // TODO This endpoint should be protected since it's an internal endpoint
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistSummaryResponseDTO createArtist(@RequestBody @Valid ArtistCreationRequestDTO creationRequestDTO) {
        ArtistSummaryResponseDTO artist = artistCommandService.createArtist(creationRequestDTO);
        log.info("Artist created: {}.", artist);
        return artist;
    }
}
