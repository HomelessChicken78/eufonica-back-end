package it.eufonica.catalogcommandservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "song",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"title", "artist_owner_id"})
        }
)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class Song {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Positive(message = "Value for \"durationSec\" must be strictly positive.")
    @Column(nullable = false)
    private Integer durationSec;

    @Pattern(
            regexp = "https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)",
            message = "Invalid URL."
    )
    private String url;

    private LocalDate publishedDate;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer amountListens = 0;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer amountLikes = 0;

    @ManyToOne(optional = false)
    @JoinColumn(name = "artist_owner_id", nullable = false)
    private Artist artistOwner;

    @ManyToMany
    @JoinTable(
            name = "song_credit",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @NotEmpty
    private Set<Artist> creditedArtists = new HashSet<>();
}