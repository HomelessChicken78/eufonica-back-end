package it.eufonica.catalogqueryservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongResponseSortOrder;
import it.eufonica.catalogqueryservice.dto.song.SongSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.song.SongShortResponseDTO;
import it.eufonica.catalogqueryservice.service.SongQueryService;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import it.eufonica.exception.dto.ValidationErrorResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/songs")
@RequiredArgsConstructor @Slf4j
public class SongController {
    private final SongQueryService songQueryService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song found."),
            @ApiResponse(responseCode = "404", description = "The song does not exist (reason: RESOURCE_NOT_FOUND).",
                    content = @Content(schema = @Schema(implementation = GeneralErrorResponseDTO.class)))
    })
    @GetMapping(path = "/{songId}", produces = APPLICATION_JSON_VALUE)
    public SongFullResponseDTO findSongById(@PathVariable UUID songId){
        SongFullResponseDTO songFullResponseDTO = songQueryService.findSongById(songId);
        log.debug("Searched and found song with id {}", songId);
        return songFullResponseDTO;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Songs matching the given filters, paginated."),
            @ApiResponse(responseCode = "400", description = "A filter range is invalid (negative value, or max lower than min), or pageNumber or pageSize is less than 1 (reason: INVALID_PAGINATION_PARAMETER for the latter two).",
                    content = @Content(schema = @Schema(oneOf = {ValidationErrorResponseDTO.class, GeneralErrorResponseDTO.class})))
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<SongShortResponseDTO> searchSongs(
            @ModelAttribute @Valid SongSearchFiltersDTO filters,
            @RequestParam(required = false) SongResponseSortOrder sortOrder,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "${SONG_PAGE_DEFAULT_SIZE:10}") Integer pageSize
    ) {
        PageResponseDTO<SongShortResponseDTO> result = songQueryService.searchSongs(filters, sortOrder, pageNumber, pageSize);
        log.debug("Songs searched: found {} results (page {}/{}).", result.getTotalElements(), pageNumber, result.getTotalPages());
        return result;
    }
}
