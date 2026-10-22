package it.eufonica.catalogcommandservice.service.validation;

import it.eufonica.exception.client.ConflictException;

import java.util.Set;
import java.util.UUID;

public interface AlbumConstraintValidator {
    /**
     * Ensures that at least one of the given artists is credited on every one of the given songs.
     *
     * <p>Enforces constraint [V.Album.tutte_le_canzoni_accreditano_uno_stesso_autore]: the album's
     * songs must share at least one common artist
     *
     * @param artistIds the candidate artist ids to check against (typically the album's credited artists)
     * @param songIds the ids of the songs that must all share a common credited artist
     *
     * @throws ConflictException if no artist in {@code artistIds} is credited on every song in {@code songIds}
     * @throws IllegalArgumentException if either {@code Set<UUID>} is {@code null} or empty
     */
    void requireCommonCreditedArtist(Set<UUID> artistIds, Set<UUID> songIds);

    /**
     * Ensures at least one artist credited on the album is credited on every song of the album.
     *
     * <p>The album, its credited artists and its songs are resolved directly from the database
     * by {@code albumId}; this overload requires the album to already be persisted.
     *
     * @param albumId the id of the album to validate
     *
     * @throws ConflictException if no artist is credited on every song of the album
     * @throws IllegalArgumentException if {@code albumId} is {@code null}
     */
    void requireCommonCreditedArtist(UUID albumId);
}
