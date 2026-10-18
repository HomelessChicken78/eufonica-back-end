package it.eufonica.catalogcommandservice.exception;

import it.eufonica.catalogcommandservice.dto.album.AlbumRefDTO;
import it.eufonica.exception.ErrorReason;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
public class AlbumCascadeConflictResponseDTO extends GeneralErrorResponseDTO {
    /*
     * Intentionally has no no-args constructor: this type is only ever serialized
     * (written by this service in the error response), never deserialized, since
     * Jackson only needs getters for serialization. GeneralErrorResponseDTO's
     * no-args constructor stays private (unchanged) — adding one here "for
     * consistency" or to allow deserialization was considered and rejected,
     * since no caller in this codebase deserializes this specific subtype
     * (upstream services parse the base GeneralErrorResponseDTO instead).
     * If a real deserialization need shows up later, add a no-args constructor
     * here and widen GeneralErrorResponseDTO's to protected so it can be called
     * via super().
     */

    private final Set<AlbumRefDTO> albumsAtRisk;

    public AlbumCascadeConflictResponseDTO(String message, ErrorReason reason, Set<AlbumRefDTO> albumsAtRisk) {
        super(message, reason);
        this.albumsAtRisk = albumsAtRisk;
    }

    public AlbumCascadeConflictResponseDTO(String message, int status, ErrorReason reason, Set<AlbumRefDTO> albumsAtRisk) {
        super(message, status, reason);
        this.albumsAtRisk = albumsAtRisk;
    }
}