package it.eufonica.catalogqueryservice.event.album;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class AlbumDeletedEvent {
    private Integer version;
    private UUID id;
}
