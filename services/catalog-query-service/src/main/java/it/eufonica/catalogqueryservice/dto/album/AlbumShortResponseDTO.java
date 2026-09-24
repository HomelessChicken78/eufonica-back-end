package it.eufonica.catalogqueryservice.dto.album;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AlbumShortResponseDTO {
    @EqualsAndHashCode.Include private UUID id;

    private String name;

    private LocalDate originalReleaseDate;
}
