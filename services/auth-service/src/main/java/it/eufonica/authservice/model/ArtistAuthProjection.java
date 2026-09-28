package it.eufonica.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "artist")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistAuthProjection {
    @EqualsAndHashCode.Include @Id private UUID id;

    @Column(nullable = false)
    private LocalDateTime registrationTimestamp;
}
