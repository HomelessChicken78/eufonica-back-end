package it.eufonica.catalogqueryservice.service;

import it.eufonica.catalogqueryservice.dto.album.AlbumFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.album.AlbumSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.album.AlbumShortResponseDTO;
import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.exception.client.BadRequestException;
import it.eufonica.catalogqueryservice.exception.client.NotFoundException;
import it.eufonica.catalogqueryservice.mapper.AlbumMapper;
import it.eufonica.catalogqueryservice.model.AlbumContainsRead;
import it.eufonica.catalogqueryservice.model.AlbumRead;
import it.eufonica.catalogqueryservice.model.ArtAlbumRead;
import it.eufonica.catalogqueryservice.model.ArtistRead;
import it.eufonica.catalogqueryservice.repository.AlbumContainsRepository;
import it.eufonica.catalogqueryservice.repository.AlbumRepository;
import it.eufonica.catalogqueryservice.repository.ArtAlbumRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Slf4j
@Service @Transactional
public class AlbumQueryServiceImpl implements AlbumQueryService {
    private final AlbumRepository albumRepository;
    private final ArtAlbumRepository artAlbumRepository;
    private final AlbumContainsRepository albumContainsRepository;
    private final AlbumMapper albumMapper;

    @Value("${ALBUM_PAGE_MAX_SIZE:50}")
    private Integer maxPageSize;

    private Specification<AlbumRead> buildSpecification(AlbumSearchFiltersDTO filters) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Add more predicate depending on if the filter's attributes are null
            // Name
            if (filters.getName() != null) {
                String escapedName = filters.getName()
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
                predicates.add(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                                "%" + escapedName + "%", '\\')
                );
                log.trace("Added filter name LIKE %{}%.", filters.getName());
            }

            // Artist name
            if (filters.getArtistName() != null) {
                String escapedArtistName = filters.getArtistName()
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");

                var subquery = query.subquery(Long.class);
                var artAlbumRoot = subquery.from(ArtAlbumRead.class);
                var artistRoot = subquery.from(ArtistRead.class);

                Predicate artAlbumLinkedToAlbum = criteriaBuilder.equal(artAlbumRoot.get("albumId"), root.get("id"));
                Predicate artAlbumLinkedToArtist = criteriaBuilder.equal(artAlbumRoot.get("artistId"), artistRoot.get("id"));
                Predicate artistNameMatches = criteriaBuilder.like(criteriaBuilder.lower(artistRoot.get("name")),
                        "%" + escapedArtistName.toLowerCase() + "%", '\\');

                subquery.select(criteriaBuilder.literal(1L))
                        .where(criteriaBuilder.and(artAlbumLinkedToAlbum, artAlbumLinkedToArtist, artistNameMatches));

                predicates.add(criteriaBuilder.exists(subquery));
                log.trace("Added filter artistName LIKE %{}%.", escapedArtistName.toLowerCase());
            }

            // Minimum songs
            if (filters.getMinSongs() != null && filters.getMinSongs() > 0) {
                var albumsWithEnoughSongs = query.subquery(UUID.class);
                var albumContainsRoot = albumsWithEnoughSongs.from(AlbumContainsRead.class);

                albumsWithEnoughSongs.select(albumContainsRoot.get("albumId"))
                        .groupBy(albumContainsRoot.get("albumId"))
                        .having(criteriaBuilder.ge(criteriaBuilder.count(albumContainsRoot), (long) filters.getMinSongs()));

                predicates.add(root.get("id").in(albumsWithEnoughSongs));
                log.trace("Added filter songCount >= {}", filters.getMinSongs());
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

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
    public PageResponseDTO<AlbumShortResponseDTO> searchAlbums(AlbumSearchFiltersDTO filters,
                                                               Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 1)
            throw new BadRequestException("pageNumber must be greater than or equal to 1.");
        if (pageSize == null || pageSize < 1)
            throw new BadRequestException("pageSize must be greater than or equal to 1.");
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        Specification<AlbumRead> spec = buildSpecification(filters);
        Page<AlbumRead> results = albumRepository.findAll(spec, PageRequest.of(pageNumber - 1, pageSize));

        List<AlbumShortResponseDTO> content = results.stream()
                .map((albumMapper::toShortResponse))
                .toList();

        return PageResponseDTO.<AlbumShortResponseDTO>builder()
                .content(content)
                .currentPage(pageNumber)
                .totalPages(results.getTotalPages())
                .totalElements(results.getTotalElements())
                .pageSize(pageSize)
                .build();
    }
}
