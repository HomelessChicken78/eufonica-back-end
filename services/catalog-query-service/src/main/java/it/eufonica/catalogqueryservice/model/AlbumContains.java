package it.eufonica.catalogqueryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "album_contains",
        uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "song_id"})
)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AlbumContains {
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @Column(nullable = false)
    private UUID albumId;

    @Column(nullable = false)
    private UUID songId;
}
