package it.eufonica.engagementservice.dto.entry;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class PlaylistEntryShortResponseDTO {
    @EqualsAndHashCode.Include private UUID createdEntryId;

    private int rankOrder;
}
