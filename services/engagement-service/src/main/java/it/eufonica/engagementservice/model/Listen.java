package it.eufonica.engagementservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "listen",
        indexes = {
                @Index(
                        name = "idx_listen_user_song_timestamp",
                        columnList = "user_id, song_id, timestamp DESC"
                )
        }
)
@AllArgsConstructor @NoArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class Listen {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include private UUID id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Column(nullable = false)
    @PositiveOrZero(message = "listen.secPlayed should always be >= 0.")
    @Builder.Default
    private Integer secPlayed = 0;
}
