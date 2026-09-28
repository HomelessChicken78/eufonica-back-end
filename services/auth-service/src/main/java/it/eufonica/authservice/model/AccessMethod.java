package it.eufonica.authservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity @Table(name = "access_method",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider_name", "provider_user_id"})
        })
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AccessMethod {
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @Column(nullable = false)
    private String providerName;

    @Column(nullable = false)
    private String providerUserId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private AppUser user;
}
