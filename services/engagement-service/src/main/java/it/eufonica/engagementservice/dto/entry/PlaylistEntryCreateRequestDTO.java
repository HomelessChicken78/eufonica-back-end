package it.eufonica.engagementservice.dto.entry;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class PlaylistEntryCreateRequestDTO {
    @NotNull(message = "songId is mandatory.")
    private UUID songId;
}
