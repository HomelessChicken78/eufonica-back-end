package it.eufonica.catalogcommandservice.event.artist;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class ArtistCreatedEvent {
    private int version;

    private UUID id;

    private String name;

    private LocalDateTime registrationDate;

    private LocalDate foundationDate;
}
