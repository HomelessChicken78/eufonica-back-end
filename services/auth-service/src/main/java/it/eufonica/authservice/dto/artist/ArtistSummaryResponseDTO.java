package it.eufonica.authservice.dto.artist;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistSummaryResponseDTO {
    @EqualsAndHashCode.Include private UUID id;
    private String name;
    private Instant registrationTimestamp;
    private LocalDate foundationDate;
}

