package it.eufonica.authservice.dto.artistrequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.model.ArtistAuthProjection;
import it.eufonica.authservice.model.ArtistRequest;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistRequestFullResponseDTO {
    @EqualsAndHashCode.Include private UUID id;

    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime timestamp;

    @JsonInclude(JsonInclude.Include.NON_NULL) // null if it's an existing-artist request
    private String requestedName;

    @JsonInclude(JsonInclude.Include.NON_NULL) // null if it's an existing-artist request
    private LocalDate requestedFoundationDate;

    private UUID requestingUserId;

    @JsonInclude(JsonInclude.Include.NON_NULL) // null if it's a new-artist request
    private UUID requestedArtistId;

    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED
    }
}
