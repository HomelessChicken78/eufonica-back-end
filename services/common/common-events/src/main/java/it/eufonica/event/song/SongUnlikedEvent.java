package it.eufonica.event.song;

import lombok.*;

import java.util.UUID;

@NoArgsConstructor @AllArgsConstructor
@Data
public class SongUnlikedEvent {
    private UUID songId;
}
