package it.eufonica.catalogcommandservice.event.song;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class SongCreatedEvent {
    private Integer version;
    private UUID id;
    private String title;
    private Integer durationSec;
    private String audio;
    private LocalDate publishedDate;
    private UUID artistOwnerId;

    @EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<ArtistCredited> creditedArtists;

    @AllArgsConstructor @NoArgsConstructor
    @Data @Builder
    public static class ArtistCredited {
        private UUID id;
        private String name;
    }
}
