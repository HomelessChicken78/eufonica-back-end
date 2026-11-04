package it.eufonica.engagementservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "song_like",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_song_app_user",
                columnNames = {"song_id", "user_id"}
        )
)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class SongLike {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include private UUID id;

    @Column(name = "song_id", nullable = false)
    private UUID songId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
