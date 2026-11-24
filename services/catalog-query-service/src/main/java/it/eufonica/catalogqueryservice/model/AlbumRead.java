package it.eufonica.catalogqueryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity @Table(name = "album")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AlbumRead {
    private Integer version;

    @EqualsAndHashCode.Include
    @Id private UUID id;

    private String name;

    private Instant pubDate;

    private LocalDate originalReleaseDate;
}
