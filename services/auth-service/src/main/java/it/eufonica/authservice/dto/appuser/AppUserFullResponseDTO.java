package it.eufonica.authservice.dto.appuser;

import lombok.*;

import java.time.LocalDateTime;
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

    private LocalDateTime registrationTimestamp;

    private ArtistSummaryDTO affiliatedArtist; // null if not affiliated
}
