package it.eufonica.authservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Table(name = "artist_request",
        check =
        @CheckConstraint(
                name = "ck_exclusive_request_type",
                constraint = "(req_name IS NOT NULL AND req_foundation_date IS NOT NULL AND req_artist_id IS NULL)" +
                        " OR (req_name IS NULL AND req_foundation_date IS NULL AND req_artist_id IS NOT NULL)"
        )
)
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistRequest {
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "req_name")
    private String requestedName;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    // Guarantees [V.ArtistRequest.richiesta_dopo_registrazione_ut] holds without a
    // runtime check anywhere an ArtistRequest is created: this value is always set
    // strictly after the requesting user must already exist to make the call.
    private LocalDateTime timestamp;

    @Column(name = "req_foundation_date")
    private LocalDate requestedFoundationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private AppUser requestingUser;

    @ManyToOne
    @JoinColumn(name = "req_artist_id")
    @ToString.Exclude
    private ArtistAuthProjection requestedArtist;

    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED
    }
}
