package it.eufonica.catalogcommandservice.event.song;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class CreditedArtistRemovedEvent {
    private int version;
    private UUID songId;
    private UUID creditedArtistId;
}
