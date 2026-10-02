package it.eufonica.authservice.dto.artist;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistSummaryDTO {
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;
}
