package it.eufonica.catalogcommandservice.service.core;

import it.eufonica.catalogcommandservice.dto.album.*;
import it.eufonica.exception.client.ConflictException;
import it.eufonica.exception.client.ForbiddenException;
import it.eufonica.exception.client.NotFoundException;
import it.eufonica.catalogcommandservice.model.Album;

import java.util.UUID;

public interface AlbumCommandService {
    /**
     * Finds an album by its id.
     *
     * @param id the unique id of the album
     *
     * @return the album associated with the given id
     * @throws NotFoundException if no album exists with the given id
     */
    Album findByIdOrThrow(UUID id);

    /**
     * Creates a new album.
     * The caller must be an authenticated user with artist access. The caller's
     * artist id is always added to the album's credited artists, no matter what
     * is in {@code request}. Each featured artist and song is checked and linked
     * to the album. At least one of the album's artists must be credited on
     * every song in the album.
     *
     * @param request the data used to create the album
     *
     * @return a summary of the newly created album
     * @throws NotFoundException if any referenced artist or song does not
     * exist
     * @throws ConflictException if an artist's dates don't fit the album's
     * dates, or if the album would have no artist credited on all its songs
     */
    AlbumSummaryResponseDTO createAlbum(AlbumCreationRequestDTO request);

    /**
     * Updates an existing album.
     * The caller must be an authenticated user with artist access, and must be
     * credited on the album. The album's id, publication date and credited
     * artists stay the same. Everything else, including the songs, is replaced
     * with the values in {@code request}. At least one of the album's artists
     * must be credited on every updated song.
     *
     * @param albumId the id of the album to update
     * @param request the new data for the album
     *
     * @return a summary of the updated album
     * @throws NotFoundException if the album does not exist, or if any
     * referenced song does not exist
     * @throws ForbiddenException if the caller is not credited on the album
     * @throws ConflictException if the original release date is after the
     * publication date, or if the album would have no artist credited on all
     * its songs
     */
    AlbumSummaryResponseDTO updateAlbum(UUID albumId, AlbumUpdateRequestDTO request);

    /**
     * Deletes an album.
     * The caller must be an authenticated user and either have artist access while being
     * credited on the album or be an admin.
     *
     * @param albumId the id of the album to delete
     *
     * @throws NotFoundException if no album with the given id exists
     * @throws ForbiddenException if the caller is both not credited on the album and not an admin
     */
    void deleteAlbum(UUID albumId);

    /**
     * Adds a song to an album.
     * The caller must be an authenticated user with artist access, and must be
     * credited on the album. After the song is added, at least one of the
     * album's artists must still be credited on every song.
     *
     * @param albumId the id of the album to add the song to
     * @param songId the id of the song to add
     *
     * @return a summary of the updated album
     * @throws NotFoundException if the album or the song does not exist
     * @throws ForbiddenException if the caller is not credited on the album
     * @throws ConflictException if the album would have no artist credited
     * on all its songs after the song is added
     */
    AlbumSummaryResponseDTO addSongToAlbum(UUID albumId, UUID songId);

    /**
     * Removes a song from an album.
     * The caller must be an authenticated user with artist access, and must be
     * credited on the album. The album can't be left without songs.
     *
     * @param albumId the id of the album to remove the song from
     * @param songId the id of the song to remove
     *
     * @return a summary of the updated album
     * @throws NotFoundException if the album does not exist, or if the song
     * is not part of the album
     * @throws ForbiddenException if the caller is not credited on the album
     * @throws ConflictException if removing the song would leave the album
     * with no songs
     */
    AlbumSummaryResponseDTO removeSongFromAlbum(UUID albumId, UUID songId);
}
