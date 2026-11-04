package it.eufonica.engagementservice.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor @AllArgsConstructor
@Data
public class PlaylistCreatedResponseDTO {
    private UUID createdPlaylistId;
}
