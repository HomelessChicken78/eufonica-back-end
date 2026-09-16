package it.eufonica.catalogqueryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "art_album",
        uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "artist_id"})
)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtAlbumRead {
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @Column(nullable = false)
    private UUID albumId;

    @Column(nullable = false)
    private UUID artistId;
}
