package it.eufonica.catalogqueryservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity @Table(name = "song")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SongRead {
    private Integer version;

    @EqualsAndHashCode.Include
    @Id private UUID id;

    private String title;

    private Integer durationSec;

    private String audio;

    private LocalDate publishedDate;

    private Integer amountListens = 0;

    private Integer amountLikes = 0;

    private UUID artistOwnerId;
}
