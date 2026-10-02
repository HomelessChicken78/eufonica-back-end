package it.eufonica.authservice.dto.appuser;

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
