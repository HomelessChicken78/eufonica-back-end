package it.eufonica.catalogcommandservice.event.song;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class CreditedArtistAddedEvent {
    private UUID songId;
    private UUID creditedArtistId;
}
