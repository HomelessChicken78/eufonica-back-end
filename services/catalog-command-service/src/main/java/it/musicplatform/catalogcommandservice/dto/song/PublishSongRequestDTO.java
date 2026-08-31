package it.musicplatform.catalogcommandservice.dto.song;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class PublishSongRequestDTO {
    private String title;

    private LocalDate publishedDate;

    private Set<UUID> creditedArtists;
}
