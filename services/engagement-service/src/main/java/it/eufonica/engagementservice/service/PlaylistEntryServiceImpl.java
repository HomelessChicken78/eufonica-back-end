package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.entry.PlaylistEntryShortResponseDTO;
import it.eufonica.engagementservice.mapper.PlaylistEntryMapper;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistEntry;
import it.eufonica.engagementservice.repository.PlaylistEntryRepository;
import it.eufonica.engagementservice.repository.PlaylistRepository;
import it.eufonica.engagementservice.repository.SongRepository;
import it.eufonica.engagementservice.security.PlaylistOwnerAccessGuard;
import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.client.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
                                "Entry not found with id: " + entryId + " inside playlist with id: " + playlist + ".",
                                CommonErrorReason.RESOURCE_NOT_FOUND
                        )
                );
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
    public PlaylistEntryShortResponseDTO addSongPlaylist(UUID playlistId, UUID songId) {
        Playlist found = queryService.findOrElseThrow(playlistId);

        accessGuard.requireOwner(found);

        PlaylistEntry savedEntry = addEntry(found, songId);
        playlistRepository.save(found);

        return entryMapper.toShortResponse(savedEntry);
    }

    @Override
    public void deletePlaylistEntry(UUID playlistId, UUID entryId) {
        Playlist foundPlaylist = queryService.findOrElseThrow(playlistId);
        PlaylistEntry foundEntry = findEntryInPlaylist(foundPlaylist, entryId);

        accessGuard.requireOwner(foundPlaylist);

        entryRepository.delete(foundEntry);
    }
}
