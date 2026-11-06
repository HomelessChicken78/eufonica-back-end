package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.dto.playlist.PlaylistCreateRequestDTO;
import it.eufonica.engagementservice.dto.playlist.PlaylistCreatedResponseDTO;
import it.eufonica.engagementservice.mapper.PlaylistMapper;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.VisibilityType;
import it.eufonica.engagementservice.repository.PlaylistEntryRepository;
import it.eufonica.engagementservice.repository.PlaylistLikeRepository;
import it.eufonica.engagementservice.repository.PlaylistRepository;
import it.eufonica.engagementservice.security.CurrentUserResolver;
import it.eufonica.engagementservice.security.PlaylistOwnerAccessGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PlaylistEntryService entryService;

    // Repository
    private final PlaylistRepository playlistRepository;
    private final PlaylistEntryRepository entryRepository;
    private final PlaylistLikeRepository playlistLikeRepository;

    // Utility & Mappers
    private final CurrentUserResolver currentUser;
    private final PlaylistOwnerAccessGuard accessGuard;
    private final PlaylistMapper playlistMapper;

    /**
     * Remove all the {@code PlaylistLike} and {@code PlaylistEntry}
     * correlated to the playlist matching the given {@code playlistId}.
     *
     * @param playlistId the id of the playlist to remove the related entities of
     */
    private void deleteRelatedPlaylistEntities(UUID playlistId) {
        playlistLikeRepository.deleteByPlaylistId(playlistId);
        entryRepository.deleteByPlaylistId(playlistId);
    }

    @Override
    public PlaylistCreatedResponseDTO createPlaylist(PlaylistCreateRequestDTO request) {
        Playlist playlist = playlistMapper.toEntity(request);
        playlist.setCreator(currentUser.getCurrentUser());

        Playlist created = playlistRepository.save(playlist);

        List<UUID> songIds = Optional.ofNullable(request.getSongIds()).orElse(List.of());

        for (UUID songId : songIds)
            entryService.addEntry(created, songId);

        return playlistMapper.toCreatedResponse(created);
    }

    @Override
    public void rename(UUID playlistId, String newName) {
        Playlist found = queryService.findOrElseThrow(playlistId);

        accessGuard.requireOwnerOrAdmin(found);

        found.setName(newName);
        playlistRepository.save(found);
    }

    @Override
    public void setVisibility(UUID playlistId, VisibilityType newVisibility) {
        Playlist found = queryService.findOrElseThrow(playlistId);

        accessGuard.requireOwner(found);

        found.setVisibility(newVisibility);
        playlistRepository.save(found);
    }

    @Override
    public void deletePlaylist(UUID playlistId) {
        Playlist found = queryService.findOrElseThrow(playlistId);

        accessGuard.requireOwnerOrAdmin(found);

        deleteRelatedPlaylistEntities(found.getId());
        playlistRepository.delete(found);
    }
}
