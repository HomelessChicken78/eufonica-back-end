package it.eufonica.catalogqueryservice.controller;

import it.eufonica.catalogqueryservice.dto.album.AlbumFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.album.AlbumSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.album.AlbumShortResponseDTO;
import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.service.AlbumQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController @RequestMapping("/albums")
@RequiredArgsConstructor @Slf4j
public class AlbumController {
    private final AlbumQueryService albumQueryService;

    @GetMapping(path = "/{albumId}", produces = APPLICATION_JSON_VALUE)
    public AlbumFullResponseDTO findAlbumById(@PathVariable UUID albumId){
        AlbumFullResponseDTO albumFullResponseDTO = albumQueryService.findAlbumById(albumId);
        log.debug("Searched and found album with id {}", albumId);
        return albumFullResponseDTO;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageResponseDTO<AlbumShortResponseDTO> searchAlbums(
            @ModelAttribute @Valid AlbumSearchFiltersDTO filters,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "${ALBUM_PAGE_DEFAULT_SIZE:10}") Integer pageSize
    ) {
        PageResponseDTO<AlbumShortResponseDTO> result = albumQueryService.searchAlbums(filters, pageNumber, pageSize);
        log.debug("Album searched: found {} results (page {}/{}).", result.getTotalElements(), pageNumber, result.getTotalPages());
        return result;
    }
}
