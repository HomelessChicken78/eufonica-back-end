package it.eufonica.authservice.dto.artistrequest;

import lombok.*;

import java.time.LocalDate;

@Data @Builder
public class CommonArtistRequestFiltersDTO {
    private RequestStatus status;
    private LocalDate since; // Midnight of the given date

    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED
    }
}
