package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.model.AppUser;
import it.eufonica.engagementservice.model.Playlist;
import it.eufonica.engagementservice.model.PlaylistLike;
import it.eufonica.engagementservice.repository.PlaylistLikeRepository;
import it.eufonica.engagementservice.repository.PlaylistRepository;
import it.eufonica.engagementservice.security.CurrentUserResolver;
import it.eufonica.engagementservice.security.PlaylistOwnerAccessGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class LikeServiceImpl implements LikeService {
    // Service
    private final PlaylistQueryService playlistService;

    // Repository
    private final PlaylistRepository playlistRepository;
    private final PlaylistLikeRepository playlistLikeRepository;

    // Utility & Mappers
    private final PlaylistOwnerAccessGuard playlistAccessGuard;
    private final CurrentUserResolver currentUser;

    @Override
    public void likePlaylist(UUID playlistId) {
        Playlist found = playlistService.findOrElseThrow(playlistId);

        playlistAccessGuard.requireViewable(found);

        AppUser appUser = currentUser.getCurrentUser();

        if (playlistLikeRepository.existsByUserAndPlaylist(appUser, found)) {
            log.debug("Playlist already liked. playlistId={}, userId={}", playlistId, appUser.getId());
            return;
        }

        PlaylistLike like = new PlaylistLike(null, found, appUser);
        playlistLikeRepository.save(like);

        found.setAmountLikes(found.getAmountLikes() + 1);
        playlistRepository.save(found);
    }

    @Override
    public void unlikePlaylist(UUID playlistId) {
        Playlist found =  playlistService.findOrElseThrow(playlistId);

        playlistAccessGuard.requireViewable(found);

        AppUser appUser = currentUser.getCurrentUser();

        if (!playlistLikeRepository.existsByUserAndPlaylist(appUser, found)) {
            log.debug("Playlist is not liked. playlistId={}, userId={}", playlistId, appUser.getId());
            return;
        }

        playlistLikeRepository.deleteByUserAndPlaylist(appUser, found);

        found.setAmountLikes(found.getAmountLikes() - 1);
        playlistRepository.save(found);
    }
}
