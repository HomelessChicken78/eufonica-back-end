package it.eufonica.catalogqueryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "artist")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArtistRead {
    private Integer version;

    @EqualsAndHashCode.Include
    @Id private UUID id;

    private String name;

    private LocalDateTime registrationDate;

    private LocalDate foundationDate;
}
