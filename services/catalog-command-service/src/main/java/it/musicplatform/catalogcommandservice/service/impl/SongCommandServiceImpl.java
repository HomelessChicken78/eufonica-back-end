package it.musicplatform.catalogcommandservice.service.impl;

import it.musicplatform.catalogcommandservice.dto.song.PublishSongRequestDTO;
import it.musicplatform.catalogcommandservice.dto.song.SongResponseDTO;
import it.musicplatform.catalogcommandservice.exception.ConflictException;
import it.musicplatform.catalogcommandservice.exception.NotFoundException;
import it.musicplatform.catalogcommandservice.mapper.SongMapper;
import it.musicplatform.catalogcommandservice.model.Artist;
import it.musicplatform.catalogcommandservice.model.Song;
import it.musicplatform.catalogcommandservice.repository.SongRepository;
import it.musicplatform.catalogcommandservice.service.ArtistCommandService;
import it.musicplatform.catalogcommandservice.service.SongCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class SongCommandServiceImpl implements SongCommandService {
    private final ArtistCommandService artistService;
    private final SongMapper mapper;
    private final Tika tika;
    private final SongRepository songRepository;

    @Value("#{'${MUSIC_FORMATS:audio/mpeg,audio/wav}'.split(',')}")
    private List<String> musicFormats;

    @Value("${MAX_AUDIO_SIZE}")
    private DataSize maxAudioSize;

    /**
     * Finds a song by its id.
     *
     * @param id the unique id of the song
     * @return the song associated with the given id
     * @throws NotFoundException if no song exists with the given id
     */
    private Song findByIdOrElseThrow(UUID id) {
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

    @Override
    public SongResponseDTO publishSong(PublishSongRequestDTO request, UUID ownerId, MultipartFile audioFile) {
        Song song = mapper.toEntity(request);

        // Add the Artist owner
        // TODO this currently uses artistId. It should use the jwt instead
        song.setArtistOwner(artistService.findByIdOrThrow(ownerId));

        // Guarantees that the constraint [V.song_owner.IS_A_song_credit] is always satisfied
        request.getCreditedArtists().add(ownerId);

        // Add credited artists
        for (UUID artistId : request.getCreditedArtists()) {
            Artist creditedArtist = artistService.findByIdOrThrow(artistId);
            addCreditedArtist(song, creditedArtist);

            // [V.song_credit.pubblicazione_dopo_fondazione]
            if (creditedArtist.getFoundationDate().isAfter(request.getPublishedDate())) {
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

        // TODO url, durationSec

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
}
