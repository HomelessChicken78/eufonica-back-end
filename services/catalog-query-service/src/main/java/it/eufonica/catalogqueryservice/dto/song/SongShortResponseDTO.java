package it.eufonica.catalogqueryservice.dto.song;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SongShortResponseDTO {
    @EqualsAndHashCode.Include
    private UUID id;

    private String title;

    private Integer durationSec;

    private Integer amountListens = 0;

    private UUID artistOwnerId;
}
