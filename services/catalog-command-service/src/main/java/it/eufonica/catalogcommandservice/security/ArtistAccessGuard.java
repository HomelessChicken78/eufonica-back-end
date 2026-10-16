package it.eufonica.catalogcommandservice.security;

import it.eufonica.catalogcommandservice.exception.CatalogErrorReason;
import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.client.ForbiddenException;
import it.eufonica.exception.server.InternalServerErrorException;
import it.eufonica.catalogcommandservice.repository.ArtistRepository;
import it.eufonica.catalogcommandservice.service.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor @Slf4j
public class ArtistAccessGuard {
    private final CurrentUserProvider currentUser;
    private final ArtistRepository artistRepository;

    // Header names
    private static final String ARTIST_ID = "X-Artist-Id";

    /**
     * Ensures the caller is authenticated as an existing artist.
     *
     * @return the caller's artist id
     * @throws ForbiddenException if the caller has no artist affiliation
     * @throws InternalServerErrorException if the affiliated artist does not exist
     */
    public UUID requireArtist() {
        String artistIdHeader = currentUser.getArtistId();
        if (artistIdHeader == null || artistIdHeader.isBlank()) {
            log.debug("requireArtist failed: no {} header present.", ARTIST_ID);
            throw new ForbiddenException("Only an artist can perform this action.", CatalogErrorReason.NOT_AN_ARTIST);
        }

        UUID callerArtistId = UUID.fromString(artistIdHeader);

        if (!artistRepository.existsById(callerArtistId)) {
            log.debug("requireArtist failed: artist {} not found in DB.", callerArtistId);
            throw new InternalServerErrorException("Authenticated artist " + callerArtistId + " not found.", CommonErrorReason.INTERNAL_ERROR);
        }

        log.debug("requireArtist succeeded for artist {}.", callerArtistId);
        return callerArtistId;
    }

    /**
     * Ensures the caller is authenticated as the artist identified by the given id.
     *
     * @param artistId the artist id the caller must match
     * @return the caller's own artist id
     * @throws ForbiddenException if the caller has no artist affiliation, or is not that artist
     * @throws InternalServerErrorException if the affiliated artist does not exist
     */
    public UUID requireArtist(UUID artistId) {
        UUID callerArtistId = requireArtist();

        if (!artistId.equals(callerArtistId))
            throw new ForbiddenException("You can only perform this action on your own artist profile.", CatalogErrorReason.ARTIST_OWNERSHIP_MISMATCH);

        return callerArtistId;
    }
}
