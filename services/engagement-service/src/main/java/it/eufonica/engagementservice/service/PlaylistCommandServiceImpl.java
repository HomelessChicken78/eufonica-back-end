package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.playlist.PlaylistCreateRequestDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistCreatedResponseDTO;
import it.eufonica.engagementservice.mapper.PlaylistMapper;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistEntry;
import it.eufonica.engagementservice.repository.PlaylistEntryRepository;
import it.eufonica.engagementservice.repository.PlaylistRepository;
import it.eufonica.engagementservice.repository.SongRepository;
import it.eufonica.engagementservice.security.CurrentUserResolver;
import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.client.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class PlaylistCommandServiceImpl implements PlaylistCommandService {
    // Service
    private final PlaylistQueryService queryService;

    // Repository
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final PlaylistEntryRepository entryRepository;

    // Utility & Mappers
    private final CurrentUserResolver currentUser;
    private final PlaylistMapper playlistMapper;

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
     * Associates the specific song with the playlist.
     *
     * <p>Creates a new PlaylistEntry containing the ids of the song and the playlist.
     * The PlaylistEntry's position is equivalent to the position of the last entry + {@code positionGap},
     * or, if the first song, {@code positionGap}.
     *
     * @param songId the id of the song to associate with the playlist
     * @param playlist the playlist to which the song is added
     *
     * @throws NotFoundException if the song with the given id does not exist
     */
    private void mapSongToPlaylist(UUID songId, Playlist playlist) {
        existSongByIdOrThrow(songId);

        long posMax = entryRepository.findLastPosition(playlist).orElse(0L);
        long nextPosition = posMax + positionGap;

        PlaylistEntry entry = new PlaylistEntry(null, nextPosition, playlist, songId);
        log.debug("Created new SongEntry. (songId={}, playlistId={}, position={})", songId, playlist.getId(), nextPosition);

        playlist.setNumberOfEntries(playlist.getNumberOfEntries() + 1);
        entryRepository.save(entry);
        log.info("Added song to playlist. (songId={}, playlistId={}, playlistName={})", songId, playlist.getId(), playlist.getName());
    }

    @Override
    public PlaylistCreatedResponseDTO createPlaylist(PlaylistCreateRequestDTO request) {
        Playlist playlist = playlistMapper.toEntity(request);
        playlist.setCreator(currentUser.getCurrentUser());

        Playlist created = playlistRepository.save(playlist);

        List<UUID> songIds = Optional.ofNullable(request.getSongIds()).orElse(List.of());

        for (UUID songId : songIds)
            mapSongToPlaylist(songId, created);

        return playlistMapper.toCreatedResponse(created);
    }

    @Override
    public void rename(UUID playlistId, String newName) {
        Playlist found = queryService.findOrElseThrow(playlistId);
        found.setName(newName);
        playlistRepository.save(found);
    }
}
