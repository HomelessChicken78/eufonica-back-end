package it.eufonica.catalogqueryservice.service;

import it.eufonica.catalogqueryservice.dto.artist.ArtistFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.artist.ArtistSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.artist.ArtistShortResponseDTO;
import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.exception.client.BadRequestException;
import it.eufonica.catalogqueryservice.exception.client.NotFoundException;
import it.eufonica.catalogqueryservice.mapper.ArtistMapper;
import it.eufonica.catalogqueryservice.model.ArtistRead;
import it.eufonica.catalogqueryservice.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Slf4j
@Service @Transactional
public class ArtistQueryServiceImpl implements ArtistQueryService {
    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Override
    public ArtistFullResponseDTO findArtistById(UUID artistId) {
        ArtistRead found = artistRepository.findById(artistId)
                .orElseThrow(
                        () -> new NotFoundException("Could not find any artist with the given id " + artistId + ".")
                );
        log.trace("Found artist {}.", found);

        ArtistFullResponseDTO response = artistMapper.toFullResponse(found);
        log.trace("Mapped artist {}", response);

        return response;
    }

    @Override
    public PageResponseDTO<ArtistShortResponseDTO> searchArtists(ArtistSearchFiltersDTO filters, Integer pageNumber, Integer pageSize) {
        return null;
    }
}