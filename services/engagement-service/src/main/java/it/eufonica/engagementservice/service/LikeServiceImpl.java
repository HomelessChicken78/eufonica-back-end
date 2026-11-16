package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.model.*;
import it.eufonica.engagementservice.repository.PlaylistLikeRepository;
import it.eufonica.engagementservice.repository.PlaylistRepository;
import it.eufonica.engagementservice.repository.SongLikeRepository;
import it.eufonica.engagementservice.repository.SongRepository;
import it.eufonica.engagementservice.security.CurrentUserResolver;
import it.eufonica.engagementservice.security.PlaylistOwnerAccessGuard;
import it.eufonica.event.outbox.OutboxEventPublisherService;
import it.eufonica.event.song.SongLikedEvent;
import it.eufonica.event.song.SongUnlikedEvent;
import it.eufonica.exception.client.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class LikeServiceImpl implements LikeService {
    // Service
    private final PlaylistQueryService playlistService;
    private final OutboxEventPublisherService eventPublisher;

    // Repository
    private final PlaylistRepository playlistRepository;
    private final PlaylistLikeRepository playlistLikeRepository;
    private final SongRepository songRepository;
    private final SongLikeRepository songLikeRepository;

    // Utility & Mappers
    private final PlaylistOwnerAccessGuard playlistAccessGuard;
    private final CurrentUserResolver currentUser;

    @Value("${SONG_ENGAGEMENT_TOPIC_NAME:song-engagement.events}")
    private String songEngagementTopicName;

    /**
     * Finds a song by its id.
     *
     * @param id the unique id of the song
     *
     * @return the song associated with the given id
     * @throws NotFoundException if no song exists with the given id
     */
    private Song findSongOrElseThrow(UUID id) {
        return songRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Song not found with id " + id + ".")
                );
    }

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

    @Override
    public void likeSong(UUID songId) {
        Song song = findSongOrElseThrow(songId);

        AppUser appUser = currentUser.getCurrentUser();

        if (songLikeRepository.existsBySongAndUser(song, appUser)) {
            log.debug("Song already liked. songId={}, userId={}", songId, appUser.getId());
            return;
        }

        SongLike like = new SongLike(null, song, appUser);
        songLikeRepository.save(like);

        eventPublisher.publish(
                songId.toString(),
                "SongLikedEvent",
                songEngagementTopicName,
                new SongLikedEvent(songId)
        );
    }

    @Override
    public void unlikeSong(UUID songId) {
        Song song = findSongOrElseThrow(songId);

        AppUser appUser = currentUser.getCurrentUser();

        if (!songLikeRepository.existsBySongAndUser(song, appUser)) {
            log.debug("Song is not liked. songId={}, userId={}", songId, appUser.getId());
            return;
        }

        songLikeRepository.deleteByUserAndSong(appUser, song);

        eventPublisher.publish(
                songId.toString(),
                "SongUnlikedEvent",
                songEngagementTopicName,
                new SongUnlikedEvent(songId)
        );
    }
}
