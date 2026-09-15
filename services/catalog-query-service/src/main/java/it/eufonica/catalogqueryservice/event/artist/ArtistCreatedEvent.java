package it.eufonica.catalogqueryservice.event.artist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
