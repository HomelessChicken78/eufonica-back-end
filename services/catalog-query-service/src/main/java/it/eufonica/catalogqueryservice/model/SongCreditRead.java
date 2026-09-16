package it.eufonica.catalogqueryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "song_credit",
        uniqueConstraints = @UniqueConstraint(columnNames = {"song_id", "artist_id"})
)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SongCreditRead {
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @Column(nullable = false)
    private UUID songId;

    @Column(nullable = false)
    private UUID artistId;
}
