package it.eufonica.events.album;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class AlbumSongRemovedEvent {
    private Integer version;
    private UUID albumId;
    private UUID songId;
}
