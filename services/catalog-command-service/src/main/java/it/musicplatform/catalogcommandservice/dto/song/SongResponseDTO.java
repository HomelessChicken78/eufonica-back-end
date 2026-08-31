package it.musicplatform.catalogcommandservice.dto.song;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SongResponseDTO {
    @EqualsAndHashCode.Include private UUID id;

    private String title;

    private Integer durationSec;

    @ToString.Exclude
    private String url;

    private LocalDate publishedDate;

    @ToString.Exclude
    private Integer amountListens;

    @ToString.Exclude
    private Integer amountLikes;

    private String artistOwner;

    @ToString.Exclude
    private Set<String> creditedArtists;
}
