package it.eufonica.engagementservice.dto.listen;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString
public class ListenResponseDTO {
    private Instant timestamp;

    @Builder.Default
    private Integer secPlayed = 0;

    private UUID songId;

    @ToString.Exclude
    private String songTitle;

    @ToString.Exclude
    private String songArtistOwnerName;
}
