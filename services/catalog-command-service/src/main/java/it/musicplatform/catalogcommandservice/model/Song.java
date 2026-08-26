package it.musicplatform.catalogcommandservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class Song {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // TODO {id2}
    @Column(nullable = false)
    private String name;

    @Positive(message = "Value for \"durationSec\" must be strictly positive.")
    @Column(nullable = false)
    private Integer durationSec;

    @Pattern(
            regexp = "https?:\\\\/\\\\/(www\\\\.)?[-a-zA-Z0-9@:%._\\\\+~#=]{1,256}\\\\.[a-zA-Z0-9()]{1,6}\\\\b([-a-zA-Z0-9()@:%_\\\\+.~#?&//=]*)",
            message = "Invalid URL."
    )
    private String url;

    private LocalDate publishedDate;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer amountListens;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer amountLikes;

    // TODO {id2}
    @ManyToOne
    private Artist artistOwner;

    @ManyToMany
    @NotEmpty
    private Set<Artist> creditedArtists;
}
