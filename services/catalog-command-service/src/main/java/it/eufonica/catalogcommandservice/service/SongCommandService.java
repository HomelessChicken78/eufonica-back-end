package it.eufonica.catalogcommandservice.service;

import it.eufonica.catalogcommandservice.dto.song.PublishSongRequestDTO;
import it.eufonica.catalogcommandservice.dto.song.SongResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface SongCommandService {
    // TODO ownerId should be taken from the jwt.
    /**
     * Publishes a new song for the artist present in the jwt.
     * <p>
     * The song owner is automatically added to the credited artists. The method
     * determines the audio file duration and store it.
     *
     * @param request   the song publication request containing song metadata
     *                  and credited artists
     * @param audioFile the audio file associated with the song
     *
     * @return the DTO representing the published song
     * @throws it.eufonica.catalogcommandservice.exception.client.NotFoundException if the owner or one of the credited artists does not exist
     * @throws it.eufonica.catalogcommandservice.exception.client.ConflictException if a song with the same title already exists for the owner,
     * or if a credited artist was founded after the song's publication date
     */
    SongResponseDTO publishSong(PublishSongRequestDTO request, UUID ownerId, MultipartFile audioFile);

    /**
     * Adds an artist to the credited artists of a song.
     *
     * @param idSong   the unique id of the song
     * @param idArtist the unique id of the artist to credit
     *
     * @return the DTO representing the updated song
     * @throws it.eufonica.catalogcommandservice.exception.client.NotFoundException if the song or artist does not exist
     */
    SongResponseDTO addCreditedArtist(UUID idSong, UUID idArtist);

    /**
     * Removes an artist from the credited artists of a song.
     *
     * @param idSong   the unique id of the song
     * @param idArtist the unique id of the artist to remove
     *
     * @return the DTO representing the updated song
     * @throws it.eufonica.catalogcommandservice.exception.client.NotFoundException if the song or artist does not exist
     * @throws it.eufonica.catalogcommandservice.exception.client.ConflictException if the artist to remove is the primary owner of the song
     */
    SongResponseDTO removeCreditedArtist(UUID idSong, UUID idArtist);
}
