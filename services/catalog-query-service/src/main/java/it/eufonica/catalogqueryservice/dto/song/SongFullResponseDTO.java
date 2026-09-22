package it.eufonica.catalogqueryservice.dto.song;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SongFullResponseDTO {
    @EqualsAndHashCode.Include
    private UUID id;

    private String title;

    private Integer durationSec;

    private String audio;

    private LocalDate publishedDate;

    private Integer amountListens = 0;

    private Integer amountLikes = 0;

    private UUID artistOwnerId;

    @ToString.Exclude
    private Set<UUID> creditedArtistsIds = new HashSet<>();
}