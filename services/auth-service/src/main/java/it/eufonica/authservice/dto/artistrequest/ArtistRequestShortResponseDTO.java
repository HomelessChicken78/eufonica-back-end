package it.eufonica.authservice.dto.artistrequest;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistRequestShortResponseDTO {
    @EqualsAndHashCode.Include private UUID id;

    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    private UUID requestingUserId;

    @JsonInclude(JsonInclude.Include.NON_NULL) // null if it's an existing-artist request
    private String requestedName;

    @JsonInclude(JsonInclude.Include.NON_NULL) // null if it's a new-artist request
    private UUID requestedArtistId;

    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED
    }
}
