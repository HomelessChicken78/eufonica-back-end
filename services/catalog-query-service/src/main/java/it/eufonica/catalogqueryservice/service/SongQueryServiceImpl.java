package it.eufonica.catalogqueryservice.service;

import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongResponseSortOrder;
import it.eufonica.catalogqueryservice.dto.song.SongSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.song.SongShortResponseDTO;
import it.eufonica.catalogqueryservice.exception.client.NotFoundException;
import it.eufonica.catalogqueryservice.mapper.SongMapper;
import it.eufonica.catalogqueryservice.model.SongCreditRead;
import it.eufonica.catalogqueryservice.model.SongRead;
import it.eufonica.catalogqueryservice.repository.SongCreditRepository;
import it.eufonica.catalogqueryservice.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor @Slf4j
@Service @Transactional
public class SongQueryServiceImpl implements SongQueryService {
    private final SongRepository songRepository;
    private final SongCreditRepository songCreditRepository;
    private final SongMapper songMapper;

    @Override
    public SongFullResponseDTO findSongById(UUID songId) {
        SongRead found = songRepository.findById(songId)
                .orElseThrow(
                        () -> new NotFoundException("Could not find any song with the given id " + songId + ".")
                );
        log.trace("Found song {}.", found);

        SongFullResponseDTO response = songMapper.toEntity(found);
        log.trace("Mapped song {}", response);

        for (SongCreditRead credit : songCreditRepository.findBySongId(songId)) {
            response.getCreditedArtistsIds().add(credit.getArtistId());
            log.trace("Added credited artist (artistId={}) to response (songId={}).", credit.getSongId(), songId);
        }

        return response;
    }

    @Override
    public PageResponseDTO<SongShortResponseDTO> searchSongs(SongSearchFiltersDTO filters, SongResponseSortOrder sortOrder,
                                                             Integer pageNumber, Integer pageSize
    ) {
        return null;
    }
}
