package it.eufonica.authservice.dto.artistrequest;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = false)
public class ArtistRequestFiltersDTO {
    private CommonArtistRequestFiltersDTO commonFilters;
    private UUID requestingUserId;
}