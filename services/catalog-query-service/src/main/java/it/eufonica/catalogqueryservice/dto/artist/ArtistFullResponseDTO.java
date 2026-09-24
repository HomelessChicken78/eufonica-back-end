package it.eufonica.catalogqueryservice.dto.artist;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistFullResponseDTO {
    @EqualsAndHashCode.Include private UUID id;

    private String name;

    private LocalDateTime registrationDate;

    private LocalDate foundationDate;
}