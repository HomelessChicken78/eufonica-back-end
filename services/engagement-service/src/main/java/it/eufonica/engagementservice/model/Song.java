package it.eufonica.engagementservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true) @ToString
public class Song {
    @Id @EqualsAndHashCode.Include private UUID id;
    private String title;
    private UUID artistOwnerId;
    private String artistOwnerName;
}
