package it.eufonica.catalogqueryservice.dto.album;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class AlbumSearchFiltersDTO {
    /**
     * Case-insensitive substring match against the album name.
     */
    private String name;

    /**
     * Case-insensitive substring match against the name of any artist linked to the album.
     */
    private String artistName;

    @Builder.Default
    @PositiveOrZero(message = "The minimum number of songs can't be negative.")
    private Integer minSongs = 0;
}

