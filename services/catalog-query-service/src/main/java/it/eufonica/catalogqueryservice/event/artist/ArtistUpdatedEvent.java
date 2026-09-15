package it.eufonica.catalogqueryservice.event.artist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
