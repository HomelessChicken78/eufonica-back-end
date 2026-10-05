package it.eufonica.authservice.dto.artistrequest;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = false)
public class SendExistingArtistRequestDTO {
    @NotNull(message = "Artist id is mandatory.")
    private UUID artistId;
}