package it.eufonica.catalogcommandservice.service.impl;

import it.eufonica.catalogcommandservice.dto.song.PublishSongRequestDTO;
import it.eufonica.catalogcommandservice.dto.song.SongResponseDTO;
import it.eufonica.catalogcommandservice.exception.client.ConflictException;
import it.eufonica.catalogcommandservice.exception.client.NotFoundException;
import it.eufonica.catalogcommandservice.dto.song.*;
import it.eufonica.catalogcommandservice.mapper.SongMapper;
import it.eufonica.catalogcommandservice.model.Artist;
import it.eufonica.catalogcommandservice.model.Song;
import it.eufonica.catalogcommandservice.repository.SongRepository;
import it.eufonica.catalogcommandservice.service.ArtistCommandService;
import it.eufonica.catalogcommandservice.service.AudioMetadataService;
import it.eufonica.catalogcommandservice.service.AudioStorageService;
import it.eufonica.catalogcommandservice.service.SongCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class SongCommandServiceImpl implements SongCommandService {
    private final ArtistCommandService artistService;
    private final SongMapper mapper;
    private final AudioMetadataService audioMetadataService;
    private final AudioStorageService audioStorageService;
    private final SongRepository songRepository;

    @Override
    public Song findByIdOrElseThrow(UUID id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Song not found with id " + id + "."));
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
    public SongResponseDTO publishSong(PublishSongRequestDTO request, UUID ownerId, MultipartFile audioFile) {
        Song song = mapper.toEntity(request);

        // Add the Artist owner
        // TODO this currently uses artistId. It should use the jwt instead
        Artist owner = artistService.findByIdOrThrow(ownerId);
        song.setArtistOwner(owner);

        // Check that the song with that title of the same author does not exist
        if (songRepository.existsByTitleAndArtistOwner(request.getTitle(), owner))
            throw new ConflictException(String.format("Song %s for artist %s already exists.", request.getTitle(), owner.getName()));

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
                        String.format("Credited Artist %s foundation date must be before or equal to Song published date.", creditedArtist.getId())
                );
            }
        }

        // Validate the audio file
        AudioMetadataDTO audioMetadata = audioMetadataService.validate(audioFile);

        // Find the duration
        int audioDurationSec = audioMetadataService.getDurationSec(audioFile, audioMetadata.getExtension());
        song.setDurationSec(audioDurationSec);

        // Send the request to the other service
        String audioObjectKeys = audioStorageService.store(audioFile, owner.getName() + "/", request.getTitle(), audioMetadata);
        song.setAudio(audioObjectKeys);

        Song savedSong = songRepository.save(song);
        return mapper.toResponse(savedSong);
    }

    @Override
    public SongResponseDTO addCreditedArtist(UUID idSong, UUID idArtist) {
        Song song = findByIdOrElseThrow(idSong);
        Artist creditedArtist = artistService.findByIdOrThrow(idArtist);

        addCreditedArtist(song, creditedArtist);

        Song savedSong = songRepository.save(song);

        return mapper.toResponse(savedSong);
    }

    @Override
    public SongResponseDTO removeCreditedArtist(UUID idSong, UUID idArtist) {
        Song song = findByIdOrElseThrow(idSong);
        Artist creditedArtist = artistService.findByIdOrThrow(idArtist);

        // Guarantee [V.song_owner.IS_A_song_credit]
        if (song.getArtistOwner().getId().equals(idArtist))
            throw new ConflictException("Cannot remove the primary owner from the song's credited artists.");

        removeCreditedArtist(song, creditedArtist);

        Song savedSong = songRepository.save(song);

        return mapper.toResponse(savedSong);
    }
}
