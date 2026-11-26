package it.eufonica.catalogcommandservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class Album {
    @Version private Integer version;

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant pubDate;

    private LocalDate originalReleaseDate;

    @ManyToMany
    @JoinTable(
            name = "art_album",
            foreignKey = @ForeignKey(name = "fk_art_album_album"),
            joinColumns = @JoinColumn(name = "album_id"),
            inverseForeignKey = @ForeignKey(name = "fk_art_album_artist"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @NotEmpty(message = "An Album should have at least one Artist.")
    private Set<Artist> artists =  new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "album_contains",
            foreignKey = @ForeignKey(name = "fk_album_contains_album"),
            joinColumns = @JoinColumn(name = "album_id"),
            inverseForeignKey = @ForeignKey(name = "fk_album_contains_song"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    @NotEmpty(message = "An album should have at least one song.")
    private Set<Song> songs = new HashSet<>();
}
