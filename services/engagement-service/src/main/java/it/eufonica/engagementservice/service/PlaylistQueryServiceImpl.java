package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.playlist.PlaylistResponseSortOrder;
import it.eufonica.engagementservice.dto.playlist.PlaylistSearchFiltersDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistShortResponseDTO;
import it.eufonica.engagementservice.mapper.PlaylistMapper;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.repository.PlaylistRepository;
import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.client.BadRequestException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.web.dto.PageResponseDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class PlaylistQueryServiceImpl implements PlaylistQueryService {
    // Service
    private final PlaylistRepository playlistRepository;
    private final PlaylistMapper playlistMapper;

    // Repository

    // Utility & Mappers

    @Value("${PLAYLIST_PAGE_MAX_SIZE:50}")
    private Integer maxPageSize;

    @Value("${PLAYLIST_DEFAULT_SORT_ORDER:LIKES_DESC}")
    private PlaylistResponseSortOrder defaultSortOrder;

    private void addRangePredicates(List<Predicate> predicates, Root<Playlist> root, CriteriaBuilder cb,
                                    String attributeName, Number min, Number max) {
        predicates.add(cb.ge(root.get(attributeName), min != null ? min : 0L));
        log.trace("Added filter {} >= {}", attributeName, min);

        if (max != null) {
            predicates.add(cb.le(root.get(attributeName), max));
            log.trace("Added filter {} <= {}", attributeName, max);
        }
    }

    private Specification<Playlist> buildSpecification(PlaylistSearchFiltersDTO filters) {
        return((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addRangePredicates(predicates, root, criteriaBuilder, "amountLikes", filters.getMinLikes(), filters.getMaxLikes());

            addRangePredicates(predicates, root, criteriaBuilder, "numberOfEntries", filters.getMinNumberOfEntries(), filters.getMaxNumberOfEntries());

            // Add more predicate depending on if the filter's attributes are null
            // Name
            if (filters.getName() != null && !filters.getName().isEmpty()) {
                String escapedName = filters.getName()
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_");
                predicates.add(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                                "%" + escapedName.toLowerCase() + "%")
                );
                log.trace("Added filter name LIKE %{}%.", escapedName);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private Sort toSorting(PlaylistResponseSortOrder sortOrder) {
        if (sortOrder == null) sortOrder = defaultSortOrder;

        return switch (sortOrder) {
            case NAME_ASC -> Sort.by(Sort.Direction.ASC, "name");
            case NAME_DESC -> Sort.by(Sort.Direction.DESC, "name");
            case LIKES_DESC -> Sort.by(Sort.Direction.DESC, "amountLikes");
            case NUMBER_OF_ENTRIES_DESC -> Sort.by(Sort.Direction.DESC, "numberOfEntries");
        };
    }

    @Override
    public Playlist findOrElseThrow(UUID id) {
        return playlistRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Could not find playlist with id " + id)
                );
    }

    @Override
    public PageResponseDTO<PlaylistShortResponseDTO> searchPlaylists(
            PlaylistSearchFiltersDTO filters, PlaylistResponseSortOrder sortOrder,
            Integer pageNumber, Integer pageSize
    ) {
        if (pageNumber == null || pageNumber < 1)
            throw new BadRequestException("pageNumber must be greater than or equal to 1.", CommonErrorReason.INVALID_PAGINATION_PARAMETER);
        if (pageSize == null || pageSize < 1)
            throw new BadRequestException("pageSize must be greater than or equal to 1.", CommonErrorReason.INVALID_PAGINATION_PARAMETER);
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;

        Specification<Playlist> spec = buildSpecification(filters);
        Page<Playlist> results = playlistRepository.findAll(spec, PageRequest.of(pageNumber - 1, pageSize, toSorting(sortOrder)));

        List<PlaylistShortResponseDTO> content = results.stream()
                .map((playlistMapper::toShortResponse))
                .toList();

        return PageResponseDTO.<PlaylistShortResponseDTO>builder()
                .content(content)
                .currentPage(pageNumber)
                .totalPages(results.getTotalPages())
                .totalElements(results.getTotalElements())
                .pageSize(pageSize)
                .build();
    }
}
