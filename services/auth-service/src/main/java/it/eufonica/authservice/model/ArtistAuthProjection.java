package it.eufonica.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "artist")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistAuthProjection {
    @EqualsAndHashCode.Include @Id private UUID id;

    @Column(nullable = false)
    private Instant registrationTimestamp;

    @Column(nullable = false)
    private String name;
}
