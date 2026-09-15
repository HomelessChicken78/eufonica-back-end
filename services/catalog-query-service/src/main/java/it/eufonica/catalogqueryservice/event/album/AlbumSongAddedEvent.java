package it.eufonica.catalogqueryservice.event.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class AlbumSongAddedEvent {
    private Integer version;
    private UUID albumId;
    private UUID songId;
    private String songTitle;
}