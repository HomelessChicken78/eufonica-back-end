package it.eufonica.engagementservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class AppUser {
    @Id
    @EqualsAndHashCode.Include private UUID id;

    @Column(nullable = false)
    private String sub;
}
