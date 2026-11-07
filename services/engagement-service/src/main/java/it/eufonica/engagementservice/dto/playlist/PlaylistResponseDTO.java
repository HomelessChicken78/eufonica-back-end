package it.eufonica.engagementservice.dto.playlist;

import it.eufonica.engagementservice.dto.entry.PlaylistEntryResponseDTO;
import it.eufonica.engagementservice.model.VisibilityType;
import it.eufonica.web.dto.PageResponseDTO;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class PlaylistResponseDTO {
    @EqualsAndHashCode.Include private UUID id;

    private String name;

    private Instant creationDate;

    private VisibilityType visibility;

    private Integer amountLikes;

    private UUID creatorId;

    private PageResponseDTO<PlaylistEntryResponseDTO> playlistEntries;
}
