package it.eufonica.engagementservice.dto.entry;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class PlaylistEntryBulkDeleteRequestDTO {
    @NotNull(message = "entryIds is mandatory.")
    @NotEmpty(message = "There must be at least an id inside entryIds.")
    private Set<UUID> entryIds;
}
