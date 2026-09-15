package it.eufonica.catalogcommandservice.event.album;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data @Builder
public class AlbumUpdatedEvent {
    private int version;
    private UUID id;
    private String name;
    private LocalDate originalReleaseDate;

    @EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<AlbumUpdatedEvent.Artist> artists;

    @EqualsAndHashCode.Exclude @ToString.Exclude
    private Set<AlbumUpdatedEvent.Song> songs;

    @AllArgsConstructor @NoArgsConstructor
    @Data @Builder
    public static class Artist {
        private UUID id;
        private String name;
    }

    @AllArgsConstructor @NoArgsConstructor
    @Data @Builder
    public static class Song {
        private UUID id;
        private String title;
    }
}
