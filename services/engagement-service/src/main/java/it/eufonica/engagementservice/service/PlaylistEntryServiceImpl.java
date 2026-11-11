package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.entry.PlaylistEntryRepositionRequestDTO;
import it.eufonica.engagementservice.dto.entry.PlaylistEntryRepositionResponseDTO;
import it.eufonica.engagementservice.dto.entry.PlaylistEntryShortResponseDTO;
import it.eufonica.engagementservice.exception.EngagementErrorReason;
import it.eufonica.engagementservice.mapper.PlaylistEntryMapper;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistEntry;
import it.eufonica.engagementservice.repository.PlaylistEntryRepository;
import it.eufonica.engagementservice.repository.PlaylistRepository;
import it.eufonica.engagementservice.repository.SongRepository;
import it.eufonica.engagementservice.security.PlaylistOwnerAccessGuard;
import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.client.BadRequestException;
import it.eufonica.exception.client.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class PlaylistEntryServiceImpl implements PlaylistEntryService {
    // Service
    private final PlaylistQueryService queryService;

    // Repository
    private final PlaylistRepository playlistRepository;
    private final PlaylistEntryRepository entryRepository;
    private final SongRepository songRepository;

    // Utility & Mappers
    private final PlaylistEntryMapper entryMapper;
    private final PlaylistOwnerAccessGuard accessGuard;

    @Value("${PLAYLIST_ENTRY_POSITION_GAP:1000}")
    private Long positionGap;

    /**
     * Search whether a song exists with the given id.
     *
     * @param id the unique id of the song
     *
     * @throws NotFoundException if no song exists with the given id
     */
    private void existSongByIdOrThrow(UUID id) {
        if (!songRepository.existsById(id))
            throw new NotFoundException("Song not found with id: " + id + ".", CommonErrorReason.RESOURCE_NOT_FOUND);
    }

    /**
     * Finds a Playlist Entry by its id inside a specific playlist
     *
     * @param playlist the playlist to search inside of
     * @param entryId the id of the entry to search for
     *
     * @return the entry inside the given playlist with the given id
     * @throws NotFoundException if either the playlist does not exist or there
     * is no entry inside the given playlist with the given id
     */
    private PlaylistEntry findEntryInPlaylist(Playlist playlist, UUID entryId) {
        return entryRepository.findByIdAndPlaylist(entryId, playlist)
                .orElseThrow(
                        () -> new NotFoundException(
                                "Entry not found with id: " + entryId + " inside playlist with id: " + playlist.getId() + ".",
                                CommonErrorReason.RESOURCE_NOT_FOUND
                        )
                );
    }

    /**
     * Rebalance the position gap of all entries within the same playlist and repositions
     * the provided {@code repositionedEntry} in the requested {@code newRankOrder}.
     *
     * <p>The position gap from an entry to the next is equal to {@code positionGap}.
     *
     * <p>The method should be called when two entries would end up sharing the same
     * position in the same playlist
     *
     * @param entries the entries of a playlist that should be rebalanced
     * @param repositionedEntry the entry of which the rank order must be changed
     * @param newRankOrder the new rank order of {@code repositionedEntry}
     *
     * @return a list of dto containing each entry id and their respective new rank order
     */
    private List<PlaylistEntryRepositionResponseDTO.CascadeReposition> rebalancePositions(
            List<PlaylistEntry> entries, PlaylistEntry repositionedEntry, int newRankOrder
    ) {
        int index = 1;
        List<PlaylistEntryRepositionResponseDTO.CascadeReposition> result = new ArrayList<>();

        for (PlaylistEntry entry : entries) {
            if (index == newRankOrder) {
                repositionedEntry.setPosition(index * positionGap);
                result.add(new PlaylistEntryRepositionResponseDTO.CascadeReposition(entry.getId(), index));
                log.trace("Repositioned entry in rank {}. entryId={}, position={}.", newRankOrder, entry.getId(), index * positionGap);
                index++;
            }
            if (repositionedEntry.getId().equals(entry.getId()))
                continue;

            entry.setPosition(index * positionGap);
            result.add(new PlaylistEntryRepositionResponseDTO.CascadeReposition(entry.getId(), index));
            log.trace("New position for entry. entryId={}, position={}, rank={}.", entry.getId(), index * positionGap, index);
            index++;
        }

        log.trace("New entry positions={}.", entries);
        entryRepository.saveAll(entries);

        return result;
    }

    @Override
    public PlaylistEntry addEntry(Playlist playlist, UUID songId) {
        existSongByIdOrThrow(songId);

        long posMax = entryRepository.findLastPosition(playlist).orElse(0L);
        long nextPosition = posMax + positionGap;

        PlaylistEntry entry = new PlaylistEntry(null, nextPosition, playlist, songId);
        log.debug("Created new SongEntry. (songId={}, playlistId={}, position={})", songId, playlist.getId(), nextPosition);

        playlist.setNumberOfEntries(playlist.getNumberOfEntries() + 1);
        entryRepository.save(entry);
        log.info("Added song to playlist. (songId={}, playlistId={}, playlistName={})", songId, playlist.getId(), playlist.getName());

        return entry;
    }

    @Override
    public List<PlaylistEntryShortResponseDTO> addSongsToPlaylist(UUID playlistId, List<UUID> songIds) {
        Playlist found = queryService.findOrElseThrow(playlistId);

        accessGuard.requireOwner(found);

        List<PlaylistEntryShortResponseDTO> addedEntries = new ArrayList<>();
        for (UUID songId : songIds) {
            PlaylistEntry entry = addEntry(found, songId);
            addedEntries.add(entryMapper.toShortResponse(entry, found.getNumberOfEntries()));
        }

        playlistRepository.save(found);

        return addedEntries;
    }

    @Override
    public void deletePlaylistEntry(UUID playlistId, UUID entryId) {
        Playlist foundPlaylist = queryService.findOrElseThrow(playlistId);
        PlaylistEntry foundEntry = findEntryInPlaylist(foundPlaylist, entryId);

        accessGuard.requireOwner(foundPlaylist);

        entryRepository.delete(foundEntry);

        foundPlaylist.setNumberOfEntries(foundPlaylist.getNumberOfEntries() - 1);
        playlistRepository.save(foundPlaylist);
    }

    @Override
    public void bulkDeleteEntries(UUID playlistId, Set<UUID> entryIds) {
        Playlist foundPlaylist = queryService.findOrElseThrow(playlistId);

        accessGuard.requireOwner(foundPlaylist);

        for (UUID entryId : entryIds) {
            PlaylistEntry entry = findEntryInPlaylist(foundPlaylist, entryId);
            entryRepository.delete(entry);
        }

        foundPlaylist.setNumberOfEntries(foundPlaylist.getNumberOfEntries() - entryIds.size());
        playlistRepository.save(foundPlaylist);
    }

    @Override
    public PlaylistEntryRepositionResponseDTO reposition(UUID playlistId, UUID entryId, PlaylistEntryRepositionRequestDTO request) {
        Playlist playlist = queryService.findOrElseThrow(playlistId);

        accessGuard.requireOwner(playlist);

        PlaylistEntry entry = findEntryInPlaylist(playlist, entryId);

        if (request.getNewRankOrder() < 1)
            throw new BadRequestException("The new rank order cannot be less than 1.", EngagementErrorReason.INVALID_RANK_ORDER);
        if (request.getNewRankOrder() > playlist.getNumberOfEntries())
            throw new BadRequestException("The new rank order cannot be greater than the number of entries in the same playlist + 1.", EngagementErrorReason.INVALID_RANK_ORDER);

        final int oneToZeroBasedOffset = 1;
        final int previousOffset = 1;

        final int previousEntryOffset = request.getNewRankOrder() - oneToZeroBasedOffset - previousOffset;
        final int nextEntryOffset = request.getNewRankOrder() - oneToZeroBasedOffset;

        Long previousEntryPosition = request.getNewRankOrder() > 1 ?
        entryRepository.findFirstByPlaylistAndIdNotOrderByPosition(
                playlist, entryId, PageRequest.of(previousEntryOffset, 1)
        )
        .map(PlaylistEntry::getPosition).orElse(0L)
        : 0;

        Optional<PlaylistEntry> nextEntry = entryRepository.findFirstByPlaylistAndIdNotOrderByPosition(
                playlist, entryId, PageRequest.of(nextEntryOffset, 1)
        );

        long nextEntryPosition = nextEntry
                .map(PlaylistEntry::getPosition)
                .orElseGet(() -> previousEntryPosition + positionGap);

        long newPosition = nextEntry.isPresent()
                ? (previousEntryPosition + nextEntryPosition) / 2
                : nextEntryPosition;

        log.trace("New potential position for entry calculated: {}. This might change if it overlaps with other positions.", newPosition);

        List<PlaylistEntryRepositionResponseDTO.CascadeReposition> cascadeRepositions = new ArrayList<>();

        if (entry.getPosition() < previousEntryPosition || entry.getPosition() > nextEntryPosition) {

            if (newPosition <= previousEntryPosition) {
                cascadeRepositions = rebalancePositions(entryRepository.findAllByPlaylist(playlist), entry, request.getNewRankOrder());
                log.info("Positions in playlists collapsed and got rebalanced.");
            } else {
                entry.setPosition(newPosition);
                entryRepository.save(entry);
                log.debug("New position for entry in playlist. playlistId={}, entryId={}, position={}", playlistId, entryId, newPosition);
            }

        } else log.debug("Position not changed, entry is already in the requested position. entryId={}", entryId);

        return new PlaylistEntryRepositionResponseDTO(entryId, request.getNewRankOrder(), cascadeRepositions);
    }
}
