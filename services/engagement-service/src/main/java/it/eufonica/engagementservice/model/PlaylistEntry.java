package it.eufonica.engagementservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "playlist_entry",
        indexes = @Index(name = "idx_playlist_entry_playlist_position", columnList = "playlist_id, position")
)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class PlaylistEntry {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include private UUID id;

    @Column(nullable = false)
    private Long position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    @ToString.Exclude
    private Playlist playlist;

    // Not a db foreign key because it allows to have a PlaylistEntry with a song that doesn't exist.
    // This way, if a song gets removed, the entries keep existing
    @Column(nullable = false)
    private UUID songId;
}
