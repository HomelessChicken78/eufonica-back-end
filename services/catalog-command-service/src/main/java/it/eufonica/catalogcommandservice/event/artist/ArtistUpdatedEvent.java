package it.eufonica.catalogcommandservice.event.artist;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class ArtistUpdatedEvent {
    private int version;

    private UUID id;

    private String name;

    private LocalDate foundationDate;
}
