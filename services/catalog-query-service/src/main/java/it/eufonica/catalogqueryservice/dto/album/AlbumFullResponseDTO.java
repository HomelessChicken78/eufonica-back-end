package it.eufonica.catalogqueryservice.dto.album;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AlbumFullResponseDTO {
    @EqualsAndHashCode.Include
    private UUID id;

    private String name;

    private LocalDateTime pubDate;

    private LocalDate originalReleaseDate;

    @Builder.Default
    private Set<UUID> artistIds = new HashSet<>();

    @Builder.Default
    private Set<UUID> songIds = new HashSet<>();
}
