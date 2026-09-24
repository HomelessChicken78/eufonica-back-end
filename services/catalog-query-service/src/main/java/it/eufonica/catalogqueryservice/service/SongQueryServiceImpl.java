package it.eufonica.catalogqueryservice.service;

import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.*;
import it.eufonica.catalogqueryservice.exception.client.BadRequestException;
import it.eufonica.catalogqueryservice.exception.client.NotFoundException;
import it.eufonica.catalogqueryservice.mapper.SongMapper;
import it.eufonica.catalogqueryservice.model.ArtistRead;
import it.eufonica.catalogqueryservice.model.SongCreditRead;
import it.eufonica.catalogqueryservice.model.SongRead;
import it.eufonica.catalogqueryservice.repository.SongCreditRepository;
import it.eufonica.catalogqueryservice.repository.SongRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Slf4j
@Service @Transactional
public class SongQueryServiceImpl implements SongQueryService {
    private final SongRepository songRepository;
    private final SongCreditRepository songCreditRepository;
    private final SongMapper songMapper;

    @Value("${SONG_PAGE_MAX_SIZE:50}")
    private Integer maxPageSize;

    @Value("${SONG_DEFAULT_SORT_ORDER:AMOUNT_LISTENS_ASC}")
    private SongResponseSortOrder defaultSortOrder;

    @SuppressWarnings("LoggingSimilarMessage")
    private void addRangePredicates(List<Predicate> predicates, Root<SongRead> root, CriteriaBuilder cb,
                                    String attributeName, Number min, Number max) {
        predicates.add(cb.ge(root.get(attributeName), min != null ? min : 0L));
        log.trace("Added filter {} >= {}", attributeName, min);

        if (max != null) {
            predicates.add(cb.le(root.get(attributeName), max));
            log.trace("Added filter {} <= {}", attributeName, max);
        }
    }

    @SuppressWarnings({"LoggingSimilarMessage", "SameParameterValue"})
    private void addRangePredicates(List<Predicate> predicates, Root<SongRead> root, CriteriaBuilder cb,
                                    String attributeName, LocalDate after, LocalDate before) {
        if (after != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get(attributeName), after));
            log.trace("Added filter {} >= {}", attributeName, after);
        }

        if (before != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get(attributeName), before));
            log.trace("Added filter {} <= {}", attributeName, before);
        }
    }

    private Specification<SongRead> buildSpecification(SongSearchFiltersDTO filters) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            /* Create a subquery. The subquery search for the owned artist and then checks if
            the filter's name is a substring of the artist's name. If it does, EXISTS returns true */
            if (filters.getArtistOwnerName() != null) {
                String escapedOwnerName = filters.getArtistOwnerName()
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
                var subquery = query.subquery(Long.class);
                var ownerRoot = subquery.from(ArtistRead.class);
                Predicate matchedOwner = criteriaBuilder.equal(ownerRoot.get("id"), root.get("artistOwnerId"));
                Predicate ownerNameAlike = criteriaBuilder.like(criteriaBuilder.lower(ownerRoot.get("name")),
                        "%" + escapedOwnerName.toLowerCase() + "%", '\\');
                subquery.select(criteriaBuilder.literal(1L))
                        .where(criteriaBuilder.and(matchedOwner, ownerNameAlike));
                predicates.add(criteriaBuilder.exists(subquery));
                log.trace("Added filter artistOwnerName LIKE %{}%.", escapedOwnerName.toLowerCase());
            }

            // Add more predicate depending on if the filter's attributes are null
            // Title
            if (filters.getTitle() != null) {
                String escapedTitle = filters.getTitle()
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
                predicates.add(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                                "%" + escapedTitle + "%", '\\')
                );
                log.trace("Added filter title LIKE %{}%.", escapedTitle);
            }

            // Duration
            addRangePredicates(predicates, root, criteriaBuilder, "durationSec", filters.getMinDurationSec(), filters.getMaxDurationSec());

            // Published Date
            addRangePredicates(predicates, root, criteriaBuilder, "publishedDate", filters.getPublishedDateAfter(), filters.getPublishedDateBefore());

            // Listens
            addRangePredicates(predicates, root, criteriaBuilder, "amountListens", filters.getMinListens(), filters.getMaxListens());

            // Likes
            addRangePredicates(predicates, root, criteriaBuilder, "amountLikes", filters.getMinLikes(), filters.getMaxLikes());

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private Sort toSorting(SongResponseSortOrder sortOrder) {
        if (sortOrder == null) sortOrder = defaultSortOrder;

        return switch (sortOrder) {
            case TITLE_ASC -> Sort.by(Sort.Direction.ASC, "title");
            case TITLE_DESC -> Sort.by(Sort.Direction.DESC, "title");
            case PUBLISHED_DATE_ASC -> Sort.by(Sort.Direction.ASC, "publishedDate");
            case PUBLISHED_DATE_DESC -> Sort.by(Sort.Direction.DESC, "publishedDate");
            case AMOUNT_LISTENS_ASC -> Sort.by(Sort.Direction.ASC, "amountListens");
            case AMOUNT_LISTENS_DESC -> Sort.by(Sort.Direction.DESC, "amountListens");
            case AMOUNT_LIKES_ASC -> Sort.by(Sort.Direction.ASC, "amountLikes");
            case AMOUNT_LIKES_DESC -> Sort.by(Sort.Direction.DESC, "amountLikes");
        };
    }

    @Override
    @Cacheable(value = "songs", key = "#songId")
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
    @Cacheable(value = "song-search")
    public PageResponseDTO<SongShortResponseDTO> searchSongs(SongSearchFiltersDTO filters, SongResponseSortOrder sortOrder,
                                                             Integer pageNumber, Integer pageSize
    ) {
        if (pageNumber == null || pageNumber < 1) throw new BadRequestException("pageNumber must be greater than or equal to 1.");
        if (pageSize == null || pageSize < 1) throw new BadRequestException("pageSize must be greater than or equal to 1.");
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        Specification<SongRead> spec = buildSpecification(filters);
        Page<SongRead> results = songRepository.findAll(spec, PageRequest.of(pageNumber - 1, pageSize, toSorting(sortOrder)));

        List<SongShortResponseDTO> content = results.stream()
                .map((songMapper::toShortResponse))
                .toList();

        return PageResponseDTO.<SongShortResponseDTO>builder()
                .content(content)
                .currentPage(pageNumber)
                .totalPages(results.getTotalPages())
                .totalElements(results.getTotalElements())
                .pageSize(pageSize)
                .build();
    }
}
