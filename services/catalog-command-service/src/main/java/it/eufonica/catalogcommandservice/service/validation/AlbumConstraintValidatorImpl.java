package it.eufonica.catalogcommandservice.service.validation;

import it.eufonica.catalogcommandservice.exception.CatalogErrorReason;
import it.eufonica.catalogcommandservice.repository.AlbumRepository;
import it.eufonica.catalogcommandservice.repository.ArtistRepository;
import it.eufonica.exception.client.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumConstraintValidatorImpl implements AlbumConstraintValidator {
    private final AlbumRepository albumRepository;

    @Override
    public void requireCommonCreditedArtist(Set<UUID> artistIds, Set<UUID> songIds) {
        if (songIds == null || songIds.isEmpty())
            throw new IllegalArgumentException("songIds cannot be null or empty.");

        if (artistIds == null || artistIds.isEmpty())
            throw new IllegalArgumentException("artistIds cannot be null or empty.");

        if (albumRepository.findCommonCreditedArtists(artistIds, songIds, songIds.size()).isEmpty())
            throw new ConflictException("No artist among the album's credited artists is credited on all songs of the album.",
                    CatalogErrorReason.ALBUM_MISSING_COMMON_CREDITED_ARTIST);
    }

    @Override
    public void requireCommonCreditedArtist(UUID albumId) {
        if (albumId == null)
            throw new IllegalArgumentException("albumId cannot be null.");

        if (albumRepository.findCommonCreditedArtists(albumId).isEmpty())
            throw new ConflictException("No artist among the album's credited artists is credited on all songs of the album.",
                    CatalogErrorReason.ALBUM_MISSING_COMMON_CREDITED_ARTIST);
    }
}
