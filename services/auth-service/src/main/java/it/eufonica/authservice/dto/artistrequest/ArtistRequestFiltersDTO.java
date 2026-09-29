package it.eufonica.authservice.dto.artistrequest;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = false)
public class ArtistRequestFiltersDTO {
    private RequestStatus status;
    private LocalDate since; // Midnight of the given date

    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED
    }
}