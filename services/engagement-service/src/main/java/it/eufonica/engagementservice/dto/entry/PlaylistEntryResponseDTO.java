package it.eufonica.engagementservice.dto.entry;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder

public class PlaylistEntryResponseDTO {
    private UUID id;
    
    private Long position;

    /**
     * Represent the id of the song the entry is tied to. Null when it references a song that doesn't exist
     */
    private UUID songId;

    /**
     * Represent the title of the song the entry is tied to. Null when it references a song that doesn't exist
     */
    private String title;

    /**
     * Represent the name of the song's owner the entry is tied to. Null when it references a song that doesn't exist
     */
    private String artistOwnerName;
}
