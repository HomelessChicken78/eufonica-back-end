package it.eufonica.authservice.dto.artistrequest;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = false)
public class SendNewArtistRequestDTO {
    @NotBlank(message = "Requested artist name is mandatory.")
    private String requestedName;

    @NotNull(message = "Requested foundation date is mandatory.")
    @PastOrPresent(message = "Requested foundation date must not be in the future.")
    private LocalDate requestedFoundationDate;
}