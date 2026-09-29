package it.eufonica.authservice.dto.artistrequest;

import java.time.LocalDate;

public class CommonArtistRequestFiltersDTO {
    private RequestStatus status;
    private LocalDate since; // Midnight of the given date

    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED
    }
}
