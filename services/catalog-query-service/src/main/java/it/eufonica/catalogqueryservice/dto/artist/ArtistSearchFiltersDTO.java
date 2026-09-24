package it.eufonica.catalogqueryservice.dto.artist;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode
public class ArtistSearchFiltersDTO {
    /**
     * Case-insensitive substring match against the artist name.
     */
    private String name;
}