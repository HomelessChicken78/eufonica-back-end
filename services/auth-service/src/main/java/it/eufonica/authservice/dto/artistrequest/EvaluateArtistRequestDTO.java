package it.eufonica.authservice.dto.artistrequest;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Data
public class EvaluateArtistRequestDTO {
    @NotNull(message = "Accepted must be specified.")
    private Boolean accepted;
}