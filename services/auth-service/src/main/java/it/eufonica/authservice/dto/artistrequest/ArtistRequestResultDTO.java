package it.eufonica.authservice.dto.artistrequest;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistRequestResultDTO {
    ArtistRequestFullResponseDTO createdRequest;

    /**
     * {@code true} when the "new artist" request could not be created as submitted.
     * This happens if another request for the same artist name was approved in the meantime.
     * In this scenario, the original request is deleted and replaced with an "existing artist"
     * request ({@code createdRequest} above). The replacement retains the original requesting
     * user and timestamp. However, it now points to the newly created artist instead of
     * asking to create a new one.
     * {@code false} if the request was created exactly as submitted.
     */
    boolean convertedToExistingArtist;
}