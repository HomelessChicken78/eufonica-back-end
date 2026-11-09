package it.eufonica.engagementservice.dto.playlist;

import it.eufonica.engagementservice.model.VisibilityType;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class PlaylistShortResponseDTO {
    private UUID id;

    private String name;

    private VisibilityType visibility;

    private Integer amountLikes;

    private Integer numberOfEntries;

    private UUID creatorId;
}
