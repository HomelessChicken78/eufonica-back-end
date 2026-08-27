package it.musicplatform.catalogcommandservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class Artist {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime registrationDate;

    @Column(nullable = false)
    private LocalDate foundationDate;

    @OneToMany(mappedBy = "artistOwner")
    private List<Song> ownedSongs = new ArrayList<>();

    @ManyToMany(mappedBy = "creditedArtists")
    private List<Song> creditedSongs = new ArrayList<>();
}
