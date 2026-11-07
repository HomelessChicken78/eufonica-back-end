package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.entry.PlaylistEntryResponseDTO;
import it.eufonica.engagementservice.dto.entry.PlaylistEntryResponseSortOrder;
import it.eufonica.engagementservice.dto.entry.PlaylistEntrySortingDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistResponseDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistResponseSortOrder;
import it.eufonica.engagementservice.dto.playlist.PlaylistSearchFiltersDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistShortResponseDTO;
import it.eufonica.engagementservice.mapper.PlaylistEntryMapper;
import it.eufonica.engagementservice.mapper.PlaylistMapper;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistEntry;
import it.eufonica.engagementservice.model.Song;
import it.eufonica.engagementservice.model.VisibilityType;
import it.eufonica.engagementservice.repository.PlaylistEntryRepository;
import it.eufonica.engagementservice.repository.PlaylistRepository;
import it.eufonica.engagementservice.repository.SongRepository;
import it.eufonica.engagementservice.security.PlaylistOwnerAccessGuard;
import it.eufonica.engagementservice.utility.StringUtils;
import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.client.BadRequestException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.security.CurrentCallerProvider;
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
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class PlaylistQueryServiceImpl implements PlaylistQueryService {
    // Service
    private final PlaylistMapper playlistMapper;

    // Repository
    private final PlaylistRepository playlistRepository;
    private final PlaylistEntryRepository entryRepository;
    private final SongRepository songRepository;

    // Utility & Mappers
    private final PlaylistEntryMapper entryMapper;
    private final CurrentCallerProvider callerProvider;
    private final PlaylistOwnerAccessGuard accessGuard;

    @Value("${PLAYLIST_PAGE_MAX_SIZE:50}")
    private Integer maxPlaylistPageSize;

    @Value("${PLAYLIST_DEFAULT_SORT_ORDER:LIKES_DESC}")
    private PlaylistResponseSortOrder defaultPlaylistSortOrder;

    @Value("${PLAYLIST_ENTRY_DEFAULT_SORT_ORDER:POSITION}")
    private PlaylistEntryResponseSortOrder defaultEntrySortOrder;

    @Value("${PLAYLIST_ENTRY_PAGE_MAX_SIZE:50}")
    private Integer maxEntryPageSize;

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
        return ((root, query, criteriaBuilder) -> {
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

            // Change the predicate depending on if the filter's attributes are false, true or null
            // Visibility
            VisibilityType targetVisibility;
            if (filters.getShowOnlyUnlisted() == null || !filters.getShowOnlyUnlisted())
                targetVisibility = VisibilityType.PUBLIC;
            else {
                callerProvider.requireAdmin();
                targetVisibility = VisibilityType.UNLISTED;
            }

            predicates.add(
                    criteriaBuilder.equal(root.get("visibility"), targetVisibility)
            );
            log.trace("Added filter visibility = '{}'", targetVisibility);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    private Sort toSorting(PlaylistResponseSortOrder sortOrder) {
        if (sortOrder == null) sortOrder = defaultPlaylistSortOrder;

        return switch (sortOrder) {
            case NAME_ASC -> Sort.by(Sort.Direction.ASC, "name");
            case NAME_DESC -> Sort.by(Sort.Direction.DESC, "name");
            case LIKES_DESC -> Sort.by(Sort.Direction.DESC, "amountLikes");
            case NUMBER_OF_ENTRIES_DESC -> Sort.by(Sort.Direction.DESC, "numberOfEntries");
        };
    }

    private PlaylistEntrySortingDTO toSorting(PlaylistEntryResponseSortOrder sortOrder) {
        if (sortOrder == null) sortOrder = defaultEntrySortOrder;

        String sortName = sortOrder.name().toLowerCase();
        PlaylistEntrySortingDTO response = new PlaylistEntrySortingDTO();

        String attributeName = StringUtils.toCamelCase(
                sortName.replace("song_", "")
                        .replace("entry_", "")
                        .replace("_asc", "")
                        .replace("_desc", "")
        );
        response.setAttributeName(attributeName);

        response.setDesc(sortName.contains("desc"));

        response.setSongAttribute(sortName.contains("song"));

        return response;
    }

    /**
     * Adds song details ({@code title} and {@code artistOwnerName}) to the entry
     *
     * @param entries the list of entries to add song details to
     *
     * @return a list of flattened dto, each containing both the most essential
     * data of the entry and the most essential data of the song
     */
    private List<PlaylistEntryResponseDTO> attachSongDetails(List<PlaylistEntry> entries) {
        // Extract all the ids of the songs
        List<UUID> songIds = entries.stream()
                .map(PlaylistEntry::getSongId)
                .toList();

        // Fetch the songs. Use findAllById to prevent fetching twice the same song
        Map<UUID, Song> entrySongs = new HashMap<>();
        for (Song song : songRepository.findAllById(songIds))
            entrySongs.put(song.getId(), song);

        return entries.stream()
                .map(e -> entryMapper.toFullResponse(e, entrySongs.get(e.getSongId())))
                .toList();
    }

    @Override
    public Playlist findOrElseThrow(UUID id) {
        Playlist found = playlistRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Could not find playlist with id " + id + ".", CommonErrorReason.RESOURCE_NOT_FOUND)
                );
        log.trace("Found playlist with id {}.", found.getId());

        return found;
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
        pageSize = pageSize > maxPlaylistPageSize ? maxPlaylistPageSize : pageSize;

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

    @Override
    public PlaylistResponseDTO findPlaylist(
            UUID playlistId, PlaylistEntryResponseSortOrder sortOrder,
            Integer pageNumber, Integer pageSize
    ) {
        if (pageNumber == null || pageNumber < 1)
            throw new BadRequestException("pageNumber must be greater than or equal to 1.", CommonErrorReason.INVALID_PAGINATION_PARAMETER);
        if (pageSize == null || pageSize < 1)
            throw new BadRequestException("pageSize must be greater than or equal to 1.", CommonErrorReason.INVALID_PAGINATION_PARAMETER);
        pageSize = pageSize > maxEntryPageSize ? maxEntryPageSize : pageSize;

        Playlist found = findOrElseThrow(playlistId);

        accessGuard.requireOwner(found);

        PlaylistEntrySortingDTO sortingDTO = toSorting(sortOrder);
        Sort sortType = JpaSort.unsafe(
                sortingDTO.isDesc() ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortingDTO.isSongAttribute() ? "s." + sortingDTO.getAttributeName() : "e." + sortingDTO.getAttributeName()
        );
        Page<PlaylistEntry> resultEntries = entryRepository.findByPlaylist(found, PageRequest.of(pageNumber - 1, pageSize, sortType));

        List<PlaylistEntryResponseDTO> contentEntries = attachSongDetails(resultEntries.getContent());

        PageResponseDTO<PlaylistEntryResponseDTO> pagedEntries = PageResponseDTO.<PlaylistEntryResponseDTO>builder()
                .content(contentEntries)
                .currentPage(pageNumber)
                .totalPages(resultEntries.getTotalPages())
                .totalElements(resultEntries.getTotalElements())
                .pageSize(pageSize)
                .build();

        return playlistMapper.toFullResponse(found, pagedEntries);
    }
}
