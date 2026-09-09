package it.eufonica.catalogcommandservice.service.impl;

import it.eufonica.catalogcommandservice.dto.album.AlbumCreationRequestDTO;
import it.eufonica.catalogcommandservice.dto.album.AlbumSummaryResponseDTO;
import it.eufonica.catalogcommandservice.exception.client.ConflictException;
import it.eufonica.catalogcommandservice.exception.client.NotFoundException;
import it.eufonica.catalogcommandservice.mapper.AlbumMapper;
import it.eufonica.catalogcommandservice.model.Album;
import it.eufonica.catalogcommandservice.model.Artist;
import it.eufonica.catalogcommandservice.model.Song;
import it.eufonica.catalogcommandservice.repository.AlbumRepository;
import it.eufonica.catalogcommandservice.service.AlbumCommandService;
import it.eufonica.catalogcommandservice.service.ArtistCommandService;
import it.eufonica.catalogcommandservice.service.SongCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class AlbumCommandServiceImpl implements AlbumCommandService {
    private final AlbumRepository albumRepository;
    private final SongCommandService songCommandService;
    private final ArtistCommandService artistCommandService;
    private final AlbumMapper mapper;

    @Override
    public Album findByIdOrThrow(UUID id) {
        return albumRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Album with id " + id + " not found.")
                );
    }

    /**
     * Validates the temporal constraints between an artist and an album.
     *
     * <p>The following constraints are enforced:
     * <ul>
     *     <li>The artist must have been founded on or before the album's original release date.</li>
     *     <li>The artist must have been registered strictly before the album's publication date.</li>
     *     <li>The artist must have been founded strictly before the album's publication date.</li>
     * </ul>
     *
     * @param artist the artist associated with the album
     * @param album the album associated with the artist
     * @param pubDate the publication date of the album
     *
     * @throws ConflictException if any of the temporal constraints is violated
     */
    private void validateArtistAlbumTemporalConstraints(Artist artist, Album album, LocalDateTime pubDate) {
        // [V.art_album.artista_fondato_prima_rilascio_ufficiale_album]
        // artist.foundation_date <= album.original_release_date
        if (artist.getFoundationDate().isAfter(album.getOriginalReleaseDate()))
            throw new ConflictException("Artist's foundation date must be before or equal to the Album's original release date.");

        // [V.art_album.artista_registrato_prima_pubblicazione_album]
        // artist.registration_timestamp < album.pub_date
        if (!artist.getRegistrationDate().isBefore(pubDate))
            throw new ConflictException("Artist's registration timestamp must be before the Album's publication date.");

        // [V.art_album.artista_fondato_prima_pubblicazione_album]
        // artist.foundation_date < album.pub_date
        if (!artist.getFoundationDate().isBefore(pubDate.toLocalDate()))
            throw new ConflictException("Artist's foundation date must be before the Album's publication date.");

        log.debug("Correctly validated the relation between artist and album.");
    }

    /**
     * Associates the specific artist with the album.
     *
     * <p>The artist is retrieved through the artist command service and validated
     * against the album's temporal constraints before being added to the album.
     *
     * @param artistId the id of the artist to associate with the album
     * @param album the album to which the artist is added
     * @param pubDate the publication date used for temporal validation
     *
     * @throws NotFoundException if the artist with the given id does not exist
     * @throws ConflictException if the artist violates the temporal constraints
     */
    private void mapArtistToAlbum(UUID artistId, Album album, LocalDateTime pubDate) {
        Artist artist = artistCommandService.findByIdOrThrow(artistId);
        validateArtistAlbumTemporalConstraints(artist, album, pubDate);

        log.info("Added artist with id {} to the album {}", artistId, album.getName());
        album.getArtists().add(artist);
    }

    /**
     * Associates the specific song with the album.
     *
     * <p>The song is retrieved through the song command service.
     *
     * @param songId the id of the song to associate with the album
     * @param album the album to which the song is added
     *
     * @throws NotFoundException if the artist with the given id does not exist
     */
    private void mapSongsToAlbum(UUID songId, Album album) {
        Song song = songCommandService.findByIdOrElseThrow(songId);

        log.info("Added song with id {} to the album {}", songId, album.getName());
        album.getSongs().add(song);
    }

    @Override
    public AlbumSummaryResponseDTO createAlbum(AlbumCreationRequestDTO request) {
        Album album = mapper.toEntity(request);

        for (UUID artId : request.getArtists())
            mapArtistToAlbum(artId, album, LocalDateTime.now());

        for (UUID songId : request.getSongs())
            mapSongsToAlbum(songId, album);

        Album savedAlbum = albumRepository.save(album);
        return mapper.toSummaryResponse(savedAlbum);
    }

    @Override
    public AlbumSummaryResponseDTO updateAlbum(UUID albumId, AlbumCreationRequestDTO request) {
        Album album = findByIdOrThrow(albumId);

        // [V.Album.rilascio_originale_prima_di_pubblicazione]
        // album.original_release_date <= album.pub_date
        if (request.getOriginalReleaseDate().isAfter(album.getPubDate().toLocalDate()))
            throw new ConflictException("Album's original release date must be before or equal to its publication date.");

        // Update the album with the new information
        // Keep album.id and album.pubDate intact
        mapper.updateEntityFromDto(request, album);

        // Clear and rebuild the collections
        album.getSongs().clear();
        log.trace("Removed songs for album with id {}.", albumId);
        album.getArtists().clear();
        log.trace("Removed artists for album with id {}.", albumId);

        for (UUID artId : request.getArtists())
            mapArtistToAlbum(artId, album, LocalDateTime.now());

        for (UUID songId : request.getSongs())
            mapSongsToAlbum(songId, album);

        Album savedAlbum = albumRepository.save(album);
        return mapper.toSummaryResponse(savedAlbum);
    }

    @Override
    public void deleteAlbum(UUID albumId) {
        Album album = findByIdOrThrow(albumId);

        album.getArtists().clear();
        album.getSongs().clear();

        albumRepository.deleteById(albumId);
    }

    @Override
    public AlbumSummaryResponseDTO addSongToAlbum(UUID albumId, UUID songId) {
        return null;
    }

    @Override
    public AlbumSummaryResponseDTO removeSongFromAlbum(UUID albumId, UUID songId) {
        return null;
    }
}
