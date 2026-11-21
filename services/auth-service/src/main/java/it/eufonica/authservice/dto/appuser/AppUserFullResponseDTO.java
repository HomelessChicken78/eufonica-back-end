package it.eufonica.authservice.dto.appuser;

import it.eufonica.authservice.dto.artist.ArtistSummaryDTO;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AppUserFullResponseDTO {
    @EqualsAndHashCode.Include
    private UUID id;

    private String displayName;

    private String firstName;

    private String middleName;

    private String lastName;

    private Instant registrationTimestamp;

    private ArtistSummaryDTO affiliatedArtist; // null if not affiliated
}
