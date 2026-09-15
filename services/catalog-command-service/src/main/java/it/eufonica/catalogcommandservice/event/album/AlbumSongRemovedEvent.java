package it.eufonica.catalogcommandservice.event.album;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class AlbumSongRemovedEvent {
    private Integer version;
    private UUID albumId;
    private UUID songId;
}