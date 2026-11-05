package it.eufonica.engagementservice.dto.playlist;

import it.eufonica.engagementservice.model.VisibilityType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Data
public class PlaylistSetVisibilityRequestDTO {
    @NotNull(message = "Visibility is mandatory.")
    private VisibilityType newVisibility = VisibilityType.PUBLIC;
}
