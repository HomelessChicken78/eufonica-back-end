package it.eufonica.engagementservice.dto.entry;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class PlaylistEntryCreateRequestDTO {
    @NotNull(message = "songIds is mandatory.")
    @NotEmpty(message = "There must be at least an id inside songIds.")
    private List<UUID> songIds;
}
