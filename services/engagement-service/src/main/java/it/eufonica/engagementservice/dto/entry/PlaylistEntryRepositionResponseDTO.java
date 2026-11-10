package it.eufonica.engagementservice.dto.entry;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class PlaylistEntryRepositionResponseDTO {
    private UUID movedEntryId;

    private int newRankOrder;

    @EqualsAndHashCode.Exclude
    private List<CascadeReposition> cascadeChanges;

    @AllArgsConstructor @NoArgsConstructor
    @Data
    public static class CascadeReposition {
        private UUID entryId;

        private int newRankOrder;
    }
}
