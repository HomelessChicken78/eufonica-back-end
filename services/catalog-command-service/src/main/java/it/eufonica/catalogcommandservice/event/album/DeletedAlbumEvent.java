package it.eufonica.catalogcommandservice.event.album;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class DeletedAlbumEvent {
    private Integer version;
    private UUID id;
}
