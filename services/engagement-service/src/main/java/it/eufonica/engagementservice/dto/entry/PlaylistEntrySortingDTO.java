package it.eufonica.engagementservice.dto.entry;

import lombok.*;
import org.springframework.data.domain.Sort;

@AllArgsConstructor @NoArgsConstructor
@Data
public class PlaylistEntrySortingDTO {
    private boolean desc;

    private String attributeName;

    private boolean songAttribute = false;
}
