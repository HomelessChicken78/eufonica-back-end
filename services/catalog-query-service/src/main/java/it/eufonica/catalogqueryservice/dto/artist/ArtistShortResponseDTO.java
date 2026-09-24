package it.eufonica.catalogqueryservice.dto.artist;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistShortResponseDTO {
    @EqualsAndHashCode.Include private UUID id;

    private String name;
}