package it.eufonica.catalogcommandservice.security;

import it.eufonica.catalogcommandservice.exception.client.ForbiddenException;
import it.eufonica.catalogcommandservice.exception.server.InternalServerErrorException;
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
            log.debug("requireArtist failed: no X-Artist-Id header present.");
            throw new ForbiddenException("Only an artist can perform this action.");
        }

        UUID callerArtistId = UUID.fromString(artistIdHeader);

        if (!artistRepository.existsById(callerArtistId)) {
            log.debug("requireArtist failed: artist {} not found in DB.", callerArtistId);
            throw new InternalServerErrorException("Authenticated artist " + callerArtistId + " not found.");
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
            throw new ForbiddenException("You can only perform this action on your own artist profile.");

        return callerArtistId;
    }
}
