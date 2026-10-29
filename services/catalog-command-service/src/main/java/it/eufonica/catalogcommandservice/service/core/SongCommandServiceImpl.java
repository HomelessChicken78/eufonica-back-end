package it.eufonica.catalogcommandservice.service.core;

import it.eufonica.catalogcommandservice.dto.album.AlbumRefDTO;
import it.eufonica.catalogcommandservice.event.song.CreditedArtistAddedEvent;
import it.eufonica.catalogcommandservice.event.song.CreditedArtistRemovedEvent;
import it.eufonica.catalogcommandservice.exception.AlbumEmptyAfterSongDeletionException;
import it.eufonica.catalogcommandservice.exception.CatalogErrorReason;
import it.eufonica.catalogcommandservice.mapper.AlbumMapper;
import it.eufonica.catalogcommandservice.model.Album;
import it.eufonica.catalogcommandservice.repository.AlbumRepository;
import it.eufonica.catalogcommandservice.service.validation.AlbumConstraintValidator;
import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.client.ConflictException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.catalogcommandservice.dto.song.*;
import it.eufonica.catalogcommandservice.mapper.SongMapper;
import it.eufonica.catalogcommandservice.model.Artist;
import it.eufonica.catalogcommandservice.model.Song;
import it.eufonica.catalogcommandservice.outbox.OutboxEventPublisherService;
import it.eufonica.catalogcommandservice.repository.SongRepository;
import it.eufonica.catalogcommandservice.security.ArtistAccessGuard;
import it.eufonica.catalogcommandservice.service.media.AudioMetadataService;
import it.eufonica.catalogcommandservice.service.media.AudioStorageService;
import it.eufonica.security.CurrentCallerProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class SongCommandServiceImpl implements SongCommandService {
    // Service
    private final ArtistCommandService artistService;
    private final AlbumDeletionService albumService;
    private final AudioMetadataService audioMetadataService;
    private final AudioStorageService audioStorageService;
    private final OutboxEventPublisherService eventPublisher;

    // Repository
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;

    // Utility & Mapper
    private final SongMapper songMapper;
    private final AlbumMapper albumMapper;
    private final CurrentCallerProvider currentCaller;
    private final ArtistAccessGuard accessGuard;
    private final AlbumConstraintValidator albumConstraintValidator;

    @Value("${SONG_TOPIC_NAME:song.events}")
    private String songTopicName;

    @Override
    public Song findByIdOrElseThrow(UUID id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Song not found with id " + id + ".", CommonErrorReason.RESOURCE_NOT_FOUND));
    }

    /**
     * Adds an artist as a credited artist of the given song.
     * Updates both sides of the bidirectional relationship.
     *
     * @param song the song to which the artist is credited
     * @param artist the artist to credit for the song
     */
    private void addCreditedArtist(Song song, Artist artist) {
        song.getCreditedArtists().add(artist);
        artist.getCreditedSongs().add(song);
    }

    /**
     * Removes an artist from the credited artists of the given song.
     * Updates both sides of the bidirectional relationship.
     *
     * @param song the song from which the artist is removed
     * @param artist the artist to remove from the song's credits
     */
    private void removeCreditedArtist(Song song, Artist artist) {
        song.getCreditedArtists().remove(artist);
        artist.getCreditedSongs().remove(song);
    }

    @Override
    public SongResponseDTO publishSong(PublishSongRequestDTO request, MultipartFile audioFile) {
        currentCaller.requireUser();
        UUID ownerId = accessGuard.requireArtist();

        Song song = songMapper.toEntity(request);

        // Add the Artist owner
        Artist owner = artistService.findByIdOrThrow(ownerId);
        song.setArtistOwner(owner);

        // Check that the song with that title of the same author does not exist
        if (songRepository.existsByTitleAndArtistOwner(request.getTitle(), owner))
            throw new ConflictException(String.format("Song %s for artist %s already exists.", request.getTitle(), owner.getName()), CatalogErrorReason.SONG_NAME_ALREADY_EXISTS_FOR_ARTIST);

        // Guarantees that the constraint [V.song_owner.IS_A_song_credit] is always satisfied
        request.getCreditedArtists().add(ownerId);

        // Add credited artists
        for (UUID artistId : request.getCreditedArtists()) {
            Artist creditedArtist = artistService.findByIdOrThrow(artistId);
            log.debug("Adding credited artist. id={}, name={}, foundationDate={}",
                    creditedArtist.getId(),
                    creditedArtist.getName(),
                    creditedArtist.getFoundationDate());
            addCreditedArtist(song, creditedArtist);

            // [V.song_credit.pubblicazione_dopo_fondazione]
            if (request.getPublishedDate() != null && creditedArtist.getFoundationDate().isAfter(request.getPublishedDate())) {
                log.warn(
                        "Invalid song credit: artist foundation date (artistId={}, foundationDate={}) is after song published date ({}).",
                        artistId,
                        creditedArtist.getFoundationDate(),
                        request.getPublishedDate()
                );

                throw new ConflictException(
                        String.format("Credited Artist %s foundation date must be before or equal to Song published date.", creditedArtist.getId()),
                        CatalogErrorReason.INVALID_CREDITED_ARTIST_FOUNDATION_DATE
                );
            }
        }

        // Validate the audio file
        AudioMetadataDTO audioMetadata = audioMetadataService.validate(audioFile);

        // Find the duration
        int audioDurationSec = audioMetadataService.getDurationSec(audioFile, audioMetadata.getExtension());
        song.setDurationSec(audioDurationSec);

        // Generate the object key
        String audioObjectKeys = audioStorageService.generateObjectKey(owner.getName() + "/", request.getTitle(), audioMetadata);
        song.setAudio(audioObjectKeys);

        Song savedSong = songRepository.saveAndFlush(song);

        // Publish the event before storing to S3. If an exception is thrown while publishing the event, nothing is uploaded on S3.
        // We use flush to instantly flush the eventOutboxRepository.save and force hibernate to execute the outbox before storing.
        eventPublisher.publish(savedSong.getId().toString(), "SongCreatedEvent",
                songTopicName, songMapper.toCreatedEvent(savedSong));
        songRepository.flush();

        /* Only after saving and publishing the event, try to store the file in S3. If the order was inverted (store before save),
        a file could be saved in S3 but the save in the local DB could fail, leaving the audio file orphan in S3 */
        audioStorageService.store(audioFile, audioObjectKeys, audioMetadata);

        return songMapper.toResponse(savedSong);
    }

    @Override
    public SongDeletionResultDTO deleteSong(UUID idSong, boolean confirmCascade) {
        // Check authorizations
        currentCaller.requireUser();
        Song toDelete = findByIdOrElseThrow(idSong);
        accessGuard.requireArtistOrAdmin(toDelete.getArtistOwner().getId());

        log.trace("Deleting song with id {} {}.",
                idSong, confirmCascade ? "with cascade enabled" : "with cascade disabled");

        // Find all albums affected by the song's deletion.
        // Then split them between albums that would remain orphan and albums that would still have at least one song
        Set<Album> albumsAffected = albumRepository.findBySongsContaining(toDelete);
        Set<Album> albumsAtRisk = new HashSet<>();
        Set<Album> survivingAlbums = new HashSet<>();
        for (Album album : albumsAffected) {
            if (album.getSongs().size() == 1)
                albumsAtRisk.add(album);
            else
                survivingAlbums.add(album);
        }

        Set<AlbumRefDTO> deletedAlbums = confirmCascade ? new HashSet<>() : null;

        // If there is at least an album that would remain orphan and confirm cascade is false, throw an exception
        if (!albumsAtRisk.isEmpty() && !confirmCascade)
            throw new AlbumEmptyAfterSongDeletionException(
                    String.format("Cannot remove the song with id %s. The following albums would remain orphan: %s.", idSong,
                            albumsAtRisk.stream().map(Album::getName).collect(Collectors.joining(", "))),
                    albumsAtRisk
            );

        // If there is at least an album that would remain orphan but confirm cascade is true, just log it
        else if (!albumsAtRisk.isEmpty())
            log.debug("The deletion of the song would lead to certain albums being orphan. " +
                    "The flag \"confirmCascade\" has been set to true, proceeding with the deletion of the albums. albums={}", albumsAtRisk);

        // Delete the song
        songRepository.delete(toDelete);
        log.trace("Deleted song with id {} from the database.", idSong);

        // Unlink each non-orphan risking album from the song (remove the link albumX - toDelete)
        for (Album survivor : survivingAlbums)
            survivor.getSongs().remove(toDelete);

        // Remove any orphan album
        for (Album orphanAlbum : albumsAtRisk) {
            deletedAlbums.add(albumMapper.toRef(orphanAlbum));

            albumService.deleteAlbum(orphanAlbum);
        }

        // Flush the change to force any change before sending the event
        songRepository.flush();
        eventPublisher.publish(idSong.toString(), "SongDeletedEvent", songTopicName, songMapper.toDeletedEvent(toDelete));

        return new SongDeletionResultDTO(idSong, deletedAlbums);
    }

    @Override
    public SongResponseDTO addCreditedArtist(UUID idSong, UUID idArtist) {
        currentCaller.requireUser();

        Song song = findByIdOrElseThrow(idSong);
        accessGuard.requireArtist(song.getArtistOwner().getId());

        Artist creditedArtist = artistService.findByIdOrThrow(idArtist);

        addCreditedArtist(song, creditedArtist);

        song.setVersion(song.getVersion() + 1);

        Song savedSong = songRepository.save(song);

        var event = new CreditedArtistAddedEvent(savedSong.getVersion(), idSong, idArtist);

        eventPublisher.publish(savedSong.getId().toString(), "SongCreditedArtistAddedEvent",
                songTopicName, event);

        return songMapper.toResponse(savedSong);
    }

    @Override
    public SongResponseDTO removeCreditedArtist(UUID idSong, UUID idArtist) {
        currentCaller.requireUser();

        Song song = findByIdOrElseThrow(idSong);
        accessGuard.requireArtist(song.getArtistOwner().getId());

        Artist creditedArtist = artistService.findByIdOrThrow(idArtist);

        // Guarantee [V.song_owner.IS_A_song_credit]
        if (song.getArtistOwner().getId().equals(idArtist))
            throw new ConflictException("Cannot remove the primary owner from the song's credited artists.", CatalogErrorReason.CANNOT_REMOVE_PRIMARY_OWNER_CREDIT);

        removeCreditedArtist(song, creditedArtist);

        song.setVersion(song.getVersion() + 1);

        Song savedSong = songRepository.saveAndFlush(song);

        for (Album album : albumRepository.findBySongsContaining(savedSong)) {
            albumConstraintValidator.requireCommonCreditedArtist(album.getId());
        }

        var event = new CreditedArtistRemovedEvent(savedSong.getVersion(), idSong, idArtist);
        eventPublisher.publish(savedSong.getId().toString(), "SongCreditedArtistRemovedEvent",
                songTopicName, event);

        return songMapper.toResponse(savedSong);
    }
}
