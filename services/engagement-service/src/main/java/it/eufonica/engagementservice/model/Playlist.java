package it.eufonica.engagementservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor @NoArgsConstructor @Builder
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class Playlist {
    @Version
    private Integer version;

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include private UUID id;

    @Column(nullable = false)
    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant creationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisibilityType visibility;

    @PositiveOrZero(message = "playlist.amountLikes should always be >= 0.")
    @Column(nullable = false)
    @Builder.Default
    private Integer amountLikes = 0;

    @Column(nullable = false)
    private UUID creatorId;

    public enum VisibilityType {
        PUBLIC, PRIVATE, UNLISTED
    }
}
