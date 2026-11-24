package it.eufonica.catalogqueryservice.dto.artist;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistFullResponseDTO {
    @EqualsAndHashCode.Include private UUID id;

    private String name;

    private Instant registrationTimestamp;

    private LocalDate foundationDate;
}