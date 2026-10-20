package it.eufonica.catalogcommandservice.service.core;

import it.eufonica.catalogcommandservice.dto.song.PublishSongRequestDTO;
import it.eufonica.catalogcommandservice.dto.song.SongDeletionResultDTO;
import it.eufonica.catalogcommandservice.dto.song.SongResponseDTO;
import it.eufonica.catalogcommandservice.exception.AlbumEmptyAfterSongDeletionException;
import it.eufonica.exception.client.ConflictException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.catalogcommandservice.model.Song;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface SongCommandService {
    /**
     * Finds a song by its id.
     *
     * @param id the unique id of the song
     * @return the song associated with the given id
     * @throws NotFoundException if no song exists with the given id
     */
    Song findByIdOrElseThrow(UUID id);

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
     * @throws NotFoundException if the owner or one of the credited artists does not exist
     * @throws ConflictException if a song with the same title already exists for the owner,
     * or if a credited artist was founded after the song's publication date
     */
    SongResponseDTO publishSong(PublishSongRequestDTO request, MultipartFile audioFile);

    /**
     * Deletes an existing song. If deleting it would leave one or more albums without any songs,
     * the operation is blocked unless {@code confirmCascade} is true, in which case those albums
     * are deleted as well.
     * <p>
     * Deletion of the associated audio file from S3 is performed asynchronously by a consumer of
     * the {@code SongDeletedEvent}, so the file may still exist for a short time after this call returns.
     *
     * @param idSong the unique id of the song to delete
     * @param confirmCascade whether the caller has already confirmed deletion of the song even if
     * it would cascade and delete one or more albums
     *
     * @return a DTO describing the outcome, including any albums deleted as part of the cascade
     * @throws NotFoundException if no song exists with the given id
     * @throws AlbumEmptyAfterSongDeletionException if deleting the song would leave one or more albums
     * without any songs and {@code confirmCascade} is false
     */
    SongDeletionResultDTO deleteSong(UUID idSong, boolean confirmCascade);

    /**
     * Adds an artist to the credited artists of a song.
     *
     * @param idSong   the unique id of the song
     * @param idArtist the unique id of the artist to credit
     *
     * @return the DTO representing the updated song
     * @throws NotFoundException if the song or artist does not exist
     */
    SongResponseDTO addCreditedArtist(UUID idSong, UUID idArtist);

    /**
     * Removes an artist from the credited artists of a song.
     *
     * @param idSong   the unique id of the song
     * @param idArtist the unique id of the artist to remove
     *
     * @return the DTO representing the updated song
     * @throws NotFoundException if the song or artist does not exist
     * @throws ConflictException if the artist to remove is the primary owner of the song
     */
    SongResponseDTO removeCreditedArtist(UUID idSong, UUID idArtist);
}
