package it.eufonica.engagementservice.service;

import it.eufonica.engagementservice.exception.EngagementErrorReason;
import it.eufonica.engagementservice.model.AppUser;
import it.eufonica.engagementservice.model.Listen;
import it.eufonica.engagementservice.model.Song;
import it.eufonica.engagementservice.repository.ListenRepository;
import it.eufonica.engagementservice.repository.SongRepository;
import it.eufonica.engagementservice.security.CurrentUserResolver;
import it.eufonica.exception.client.BadRequestException;
import it.eufonica.exception.client.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class ListenServiceImpl implements ListenService {
    // Service

    // Repository
    private final SongRepository songRepository;
    private final ListenRepository listenRepository;

    // Utility & Mappers
    private final CurrentUserResolver currentUser;

    @Value("${LISTEN_MIN_SEC_PLAYED_CAP:3}")
    private int minSecPlayedCap;

    @Value("${LISTEN_MIN_SEC_PLAYED_PERCENTAGE:0.5}")
    private double minSecPlayedPercentage;

    @Value("${LISTEN_COOLDOWN_SECONDS:60}")
    private int cooldownSeconds;

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

    /**
     * Check whether the {@code secPlayed} meets the threshold.
     *
     * <p>The threshold is equal to a percentage of {@code song.durationSec},
     * capped to a configured maximum
     *
     * @param song the song to extract the {@code song.durationSec} from
     * @param secPlayed the seconds played of the listen to validate the
     * duration against
     *
     * @return <ul>
     *     <li>{@code true} if the {@code secPlayed} meets the threshold</li>
     *     <li>{@code false} if the {@code secPlayed} is less than the threshold</li>
     * </ul>
     */
    private boolean meetsMinimumPlayThreshold(Song song, int secPlayed) {
        double threshold = Math.min(song.getDurationSec() * minSecPlayedPercentage, minSecPlayedCap);
        boolean meetsThreshold = secPlayed >= threshold;

        log.debug("Checked minimum play threshold. songId={}, secPlayed={}, threshold={}, meetsThreshold={}.",
                song.getId(), secPlayed, threshold, meetsThreshold);

        return meetsThreshold;
    }

    /**
     * Check whether the given {@code appUser} has recently listened to the
     * given {@code song}.
     *
     * <p>A listen is considered "recent" if it occurred less than
     * {@code LISTEN_COOLDOWN_SECONDS} seconds ago.
     *
     * @param appUser the user that listened to the song
     * @param song the song listened
     *
     * @return <ul>
     *     <li>{@code true} if the last listen is considered recent</li>
     *     <li>{@code false} if the last listen is not considered recent or there
     *     is no listen between the given song and the given user</li>
     * </ul>
     */
    private boolean hasRecentListen(AppUser appUser, Song song) {
        Listen latestListen = listenRepository.findFirstByUserAndSongOrderByTimestampDesc(appUser, song)
                .orElse(null);

        if (latestListen == null)
            return false;

        Instant cooldownEnd = latestListen.getTimestamp().plusSeconds(cooldownSeconds);
        boolean isRecent = cooldownEnd.isAfter(Instant.now());

        log.debug("Checked recent listen. songId={}, userId={}, latestListenId={}, cooldownEnd={}, isRecent={}.",
                song.getId(), appUser.getId(), latestListen.getId(), cooldownEnd, isRecent);

        return isRecent;
    }

    @Override
    public void listen(UUID songId, int secPlayed) {
        Song song = findSongOrElseThrow(songId);

        if (secPlayed > song.getDurationSec())
            throw new BadRequestException(
                    String.format("Seconds played for song %s cannot be greater or equal to the song's duration (%d seconds).", song.getTitle(), song.getDurationSec()),
                    EngagementErrorReason.INVALID_SEC_PLAYED
            );

        if (!meetsMinimumPlayThreshold(song, secPlayed)) return;

        AppUser appUser = currentUser.getCurrentUser();

        if (hasRecentListen(appUser, song)) return;

        Listen listen = Listen.builder()
                .song(song)
                .user(appUser)
                .secPlayed(secPlayed)
                .build();
        listenRepository.save(listen);
        log.debug("Created new Listen. songId={}, userId={}.", songId, appUser.getId());

        // TODO send event on kafka
    }
}
