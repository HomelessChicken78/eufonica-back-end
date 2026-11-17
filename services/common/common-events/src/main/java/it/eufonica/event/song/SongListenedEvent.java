package it.eufonica.event.song;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class SongListenedEvent {
    private UUID songId;
}
