package it.eufonica.catalogcommandservice.controller.external;

import it.eufonica.catalogcommandservice.dto.artist.ArtistCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.artist.ArtistSummaryResponseDTO;
import it.eufonica.catalogcommandservice.service.core.ArtistCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/artists")
@RequiredArgsConstructor @Slf4j
public class ArtistController {
    private final ArtistCommandService artistCommandService;

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ArtistSummaryResponseDTO updateArtist(@RequestBody @Valid ArtistCreationRequestDTO creationRequestDTO) {
        ArtistSummaryResponseDTO artist = artistCommandService.updateArtist(creationRequestDTO);
        log.info("Artist updated: {}.", artist);
        return artist;
    }
}
