package it.eufonica.catalogqueryservice.controller;

import it.eufonica.catalogqueryservice.dto.artist.ArtistFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.artist.ArtistSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.artist.ArtistShortResponseDTO;
import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.service.ArtistQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/artists")
@RequiredArgsConstructor @Slf4j
public class ArtistController {
    private final ArtistQueryService artistQueryService;

    @GetMapping(path = "/{artistId}", produces = APPLICATION_JSON_VALUE)
    public ArtistFullResponseDTO findArtistById(@PathVariable UUID artistId){
        ArtistFullResponseDTO artistFullResponseDTO = artistQueryService.findArtistById(artistId);
        log.debug("Searched and found artist with id {}", artistId);
        return artistFullResponseDTO;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<ArtistShortResponseDTO> searchArtists(
            @ModelAttribute @Valid ArtistSearchFiltersDTO filters,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "${ALBUM_PAGE_DEFAULT_SIZE:10}") Integer pageSize
    ) {
        PageResponseDTO<ArtistShortResponseDTO> result = artistQueryService.searchArtists(filters, pageNumber, pageSize);
        log.debug("Artist searched: found {} results (page {}/{}).", result.getTotalElements(), pageNumber, result.getTotalPages());
        return result;
    }
}
