package it.eufonica.catalogcommandservice.dto.album;

import lombok.*;

import java.util.UUID;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor @AllArgsConstructor
public class AlbumRefDTO {
    @EqualsAndHashCode.Include private UUID id;

    private String name;
}
