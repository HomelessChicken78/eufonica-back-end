package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistEntry;
import it.eufonica.engagementservice.repository.PlaylistEntryRepository;
import it.eufonica.engagementservice.repository.SongRepository;
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

    // Repository
    private final SongRepository songRepository;

    // Utility & Mappers
    private final PlaylistEntryRepository entryRepository;

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

    @Override
    public void addEntry(UUID songId, Playlist playlist) {
        existSongByIdOrThrow(songId);

        long posMax = entryRepository.findLastPosition(playlist).orElse(0L);
        long nextPosition = posMax + positionGap;

        PlaylistEntry entry = new PlaylistEntry(null, nextPosition, playlist, songId);
        log.debug("Created new SongEntry. (songId={}, playlistId={}, position={})", songId, playlist.getId(), nextPosition);

        playlist.setNumberOfEntries(playlist.getNumberOfEntries() + 1);
        entryRepository.save(entry);
        log.info("Added song to playlist. (songId={}, playlistId={}, playlistName={})", songId, playlist.getId(), playlist.getName());
    }
}
