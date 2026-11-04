package it.eufonica.engagementservice.dto.playlist;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class PlaylistShortResponseDTO {
    private UUID id;

    private String name;

    private Integer amountLikes;

    private UUID creatorId;
}
