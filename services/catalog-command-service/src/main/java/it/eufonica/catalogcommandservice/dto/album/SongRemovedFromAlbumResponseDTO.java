package it.eufonica.catalogcommandservice.dto.album;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data @Builder
public class SongRemovedFromAlbumResponseDTO {
    private UUID albumId;

    private boolean albumRemoved;

    /**
     * Field should be null if albumRemoved=true
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer remainingSongCount;
}
