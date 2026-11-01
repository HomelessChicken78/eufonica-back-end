package it.eufonica.events.song;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class CreditedArtistAddedEvent {
    private int version;
    private UUID songId;
    private UUID creditedArtistId;
}
