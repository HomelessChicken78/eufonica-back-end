package it.eufonica.catalogcommandservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "album",
        check = @CheckConstraint(
                name = "chk_album_release_before_pub",
                constraint = "original_release_date <= pub_date"
        )
)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class Album {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate pubDate;

    private LocalDate originalReleaseDate;

    @ManyToMany
    @JoinTable(
            name = "art_album",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @NotEmpty(message = "An Album should have at least one Artist.")
    private List<Artist> artists =  new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "album_contains",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs = new ArrayList<>();
}
