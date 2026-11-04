package it.eufonica.engagementservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "playlist_entry",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_playlist_position",
                columnNames = {"playlist_id", "position"}
        )
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

    @Column(nullable = false)
    private UUID songId;
}
