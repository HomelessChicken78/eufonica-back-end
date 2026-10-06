package it.eufonica.catalogcommandservice.security;

import it.eufonica.catalogcommandservice.exception.client.ForbiddenException;
import it.eufonica.catalogcommandservice.exception.server.InternalServerErrorException;
import it.eufonica.catalogcommandservice.repository.ArtistRepository;
import it.eufonica.catalogcommandservice.service.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component @RequiredArgsConstructor
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
        if (artistIdHeader == null || artistIdHeader.isBlank())
            throw new ForbiddenException("Only an artist can perform this action.");

        UUID callerArtistId = UUID.fromString(artistIdHeader);

        if (!artistRepository.existsById(callerArtistId))
            throw new InternalServerErrorException("Authenticated artist " + callerArtistId + " not found.");

        return callerArtistId;
    }
}
