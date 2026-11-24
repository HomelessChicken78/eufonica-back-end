package it.eufonica.event.artist;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class ArtistCreatedEvent {
    private int version;

    private UUID id;

    private String name;

    private Instant registrationTimestamp;

    private LocalDate foundationDate;
}
