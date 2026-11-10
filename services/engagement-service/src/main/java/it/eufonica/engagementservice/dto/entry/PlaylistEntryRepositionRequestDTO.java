package it.eufonica.engagementservice.dto.entry;

import jakarta.validation.constraints.Positive;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Data
public class PlaylistEntryRepositionRequestDTO {
    @Positive(message = "newRankOrder must be grater than 0.")
    private int newRankOrder;
}
