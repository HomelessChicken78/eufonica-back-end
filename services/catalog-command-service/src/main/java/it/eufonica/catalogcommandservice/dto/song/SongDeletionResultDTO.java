package it.eufonica.catalogcommandservice.dto.song;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.eufonica.catalogcommandservice.dto.album.AlbumRefDTO;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor @AllArgsConstructor
public class SongDeletionResultDTO {
    private UUID idSong;

    /**
     * The albums deleted as a cascading side effect of the song deletion.
     * <p>
     * {@code null} if the caller did not pass {@code confirmCascade=true} (the
     * concept of cascade does not apply — the song's deletion plainly happened
     * outside any cascade flow). An empty set if {@code confirmCascade=true}
     * was passed but, when the deletion was actually carried out, deleting the
     * song did not end up emptying any album (e.g. because the album state had
     * changed since an earlier 409 response, as deletion always re-checks from
     * scratch).
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<AlbumRefDTO> deletedAlbums;
}