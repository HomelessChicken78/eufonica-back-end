package it.eufonica.event.song;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor @AllArgsConstructor
public class SongDeletedEvent {
    private Integer version;

    private UUID id;

    private String audio;
}
