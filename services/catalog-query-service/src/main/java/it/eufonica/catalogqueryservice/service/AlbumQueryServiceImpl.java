package it.eufonica.catalogqueryservice.service;

import it.eufonica.catalogqueryservice.dto.album.AlbumFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.album.AlbumSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.album.AlbumShortResponseDTO;
import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.exception.client.NotFoundException;
import it.eufonica.catalogqueryservice.mapper.AlbumMapper;
import it.eufonica.catalogqueryservice.model.AlbumContainsRead;
import it.eufonica.catalogqueryservice.model.AlbumRead;
import it.eufonica.catalogqueryservice.model.ArtAlbumRead;
import it.eufonica.catalogqueryservice.repository.AlbumContainsRepository;
import it.eufonica.catalogqueryservice.repository.AlbumRepository;
import it.eufonica.catalogqueryservice.repository.ArtAlbumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor @Slf4j
@Service @Transactional
public class AlbumQueryServiceImpl implements AlbumQueryService {
    private final AlbumRepository albumRepository;
    private final ArtAlbumRepository artAlbumRepository;
    private final AlbumContainsRepository albumContainsRepository;
    private final AlbumMapper albumMapper;

    @Override
    public AlbumFullResponseDTO findAlbumById(UUID albumId) {
        AlbumRead found = albumRepository.findById(albumId)
                .orElseThrow(
                        () -> new NotFoundException("Could not find any album with the given id " + albumId + ".")
                );
        log.trace("Found album {}.", found);

        AlbumFullResponseDTO response = albumMapper.toFullResponse(found);
        log.trace("Mapped album {}", response);

        for (ArtAlbumRead art : artAlbumRepository.findByAlbumId(albumId)) {
            response.getArtistIds().add(art.getArtistId());
            log.trace("Added artist (artistId={}) to response (albumId={}).", art.getArtistId(), albumId);
        }

        for (AlbumContainsRead songLink : albumContainsRepository.findByAlbumId(albumId)) {
            response.getSongIds().add(songLink.getSongId());
            log.trace("Added song (songId={}) to response (albumId={}).", songLink.getSongId(), albumId);
        }

        return response;
    }

    @Override
    public PageResponseDTO<AlbumShortResponseDTO> searchAlbums(AlbumSearchFiltersDTO filters, Integer pageNumber, Integer pageSize) {
        return null;
    }
}
