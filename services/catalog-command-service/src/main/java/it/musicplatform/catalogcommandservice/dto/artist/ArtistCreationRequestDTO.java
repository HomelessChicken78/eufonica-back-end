package it.musicplatform.catalogcommandservice.dto.artist;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
public class ArtistCreationRequestDTO {
    private String name;
    private LocalDate foundationDate;
}
