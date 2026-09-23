package it.eufonica.catalogqueryservice.controller;

import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongResponseSortOrder;
import it.eufonica.catalogqueryservice.dto.song.SongSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.song.SongShortResponseDTO;
import it.eufonica.catalogqueryservice.service.SongQueryService;
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

    @GetMapping(path = "/{songId}", produces = APPLICATION_JSON_VALUE)
    public SongFullResponseDTO findSongById(@PathVariable UUID songId){
        SongFullResponseDTO songFullResponseDTO = songQueryService.findSongById(songId);
        log.debug("Searched and found song with id {}", songId);
        return songFullResponseDTO;
    }

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
