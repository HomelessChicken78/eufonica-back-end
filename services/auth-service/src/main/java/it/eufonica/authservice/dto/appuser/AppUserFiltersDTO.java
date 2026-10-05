package it.eufonica.authservice.dto.appuser;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = false)
public class AppUserFiltersDTO {
    private String displayName;
    private Boolean hasAffiliatedArtist;
    private LocalDate registeredSince;
}
