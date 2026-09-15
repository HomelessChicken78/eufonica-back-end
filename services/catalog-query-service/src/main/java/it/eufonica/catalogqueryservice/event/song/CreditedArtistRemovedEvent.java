package it.eufonica.catalogqueryservice.event.song;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class CreditedArtistRemovedEvent {
    private int version;
    private UUID songId;
    private UUID creditedArtistId;
}
