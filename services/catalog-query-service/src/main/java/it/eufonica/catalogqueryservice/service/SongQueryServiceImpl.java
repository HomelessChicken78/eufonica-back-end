package it.eufonica.catalogqueryservice.service;

import it.eufonica.catalogqueryservice.dto.common.PageResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongFullResponseDTO;
import it.eufonica.catalogqueryservice.dto.song.SongResponseSortOrder;
import it.eufonica.catalogqueryservice.dto.song.SongSearchFiltersDTO;
import it.eufonica.catalogqueryservice.dto.song.SongShortResponseDTO;
import it.eufonica.catalogqueryservice.exception.client.NotFoundException;
import it.eufonica.catalogqueryservice.mapper.SongMapper;
import it.eufonica.catalogqueryservice.model.ArtistRead;
import it.eufonica.catalogqueryservice.model.SongCreditRead;
import it.eufonica.catalogqueryservice.model.SongRead;
import it.eufonica.catalogqueryservice.repository.SongCreditRepository;
import it.eufonica.catalogqueryservice.repository.SongRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Slf4j
@Service @Transactional
public class SongQueryServiceImpl implements SongQueryService {
    private final SongRepository songRepository;
    private final SongCreditRepository songCreditRepository;
    private final SongMapper songMapper;

    private Specification<SongRead> buildSpecification(SongSearchFiltersDTO filters, SongResponseSortOrder sortOrder) {
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
                predicates.add(
                        criteriaBuilder.like(root.get("title"),
                                "%" + filters.getTitle().toLowerCase() + "%")
                );
                log.trace("Added filter title LIKE %{}%.", filters.getTitle());
            }

            // Duration
            // Minimum
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("durationSec"),
                            filters.getMinDurationSec() != null ? filters.getMinDurationSec() : 0L)
            );
            log.trace("Added filter durationSec >= {}", filters.getMinDurationSec());

            // Maximum
            if (filters.getMaxDurationSec() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("durationSec"), filters.getMaxDurationSec())
                );
                log.trace("Added filter durationSec <= {}", filters.getMaxDurationSec());
            }

            // Published Date
            // After
            if (filters.getPublishedDateAfter() != null) {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("publishedDate"), filters.getPublishedDateAfter())
                );
                log.trace("Added filter publishedDate >= '{}'", filters.getPublishedDateAfter());
            }

            // Before
            if (filters.getPublishedDateBefore() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("publishedDate"), filters.getPublishedDateBefore())
                );
                log.trace("Added filter publishedDate <= '{}'", filters.getPublishedDateBefore());
            }

            // Listens
            // Minimum
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("amountListens"),
                            filters.getMinListens() != null ? filters.getMinListens() : 0L)
            );
            log.trace("Added filter amountListens >= {}", filters.getMinListens());

            // Maximum
            if (filters.getMaxListens() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("amountListens"), filters.getMaxListens())
                );
                log.trace("Added filter amountListens <= {}", filters.getMaxListens());
            }

            // Likes
            // Minimum
            predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("amountLikes"),
                            filters.getMinLikes() != null ? filters.getMinLikes() : 0L)
            );
            log.trace("Added filter amountLikes >= {}", filters.getMinLikes());

            // Maximum
            if (filters.getMaxLikes() != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("amountLikes"), filters.getMaxLikes())
                );
                log.trace("Added filter amountLikes <= {}", filters.getMaxLikes());
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private static Sort toSorting(SongResponseSortOrder sortOrder) {
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
