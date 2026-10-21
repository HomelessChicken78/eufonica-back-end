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
     */
    void requireCommonCreditedArtist(Set<UUID> artistIds, Set<UUID> songIds);
}
