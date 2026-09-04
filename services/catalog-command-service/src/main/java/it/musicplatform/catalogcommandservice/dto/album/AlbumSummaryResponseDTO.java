package it.musicplatform.catalogcommandservice.dto.album;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AlbumSummaryResponseDTO {
    @EqualsAndHashCode.Include private UUID id;

    private String name;

    private LocalDate originalReleaseDate;

    private Integer numberOfSongs;
}
