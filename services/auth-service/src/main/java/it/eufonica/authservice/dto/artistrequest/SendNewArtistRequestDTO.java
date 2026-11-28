package it.eufonica.authservice.dto.artistrequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = false)
public class SendNewArtistRequestDTO {
    @NotBlank(message = "Requested artist's name can't be empty")
    @Pattern(
            regexp = "^[A-Za-z0-9_][A-Za-z0-9_ ]{1,18}[A-Za-z0-9_]$",
            message = "Requested name must be 3–20 characters long, cannot start or end with a space, " +
                    "and may contain only letters, numbers, underscores, or spaces."
    )
    private String requestedName;

    @NotNull(message = "Requested foundation date is mandatory.")
    @PastOrPresent(message = "Requested foundation date must not be in the future.")
    private LocalDate requestedFoundationDate;
}