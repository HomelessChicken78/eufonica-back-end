package it.eufonica.engagementservice.dto.playlist;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import it.eufonica.engagementservice.model.VisibilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor @Builder
@Data
public class PlaylistCreateRequestDTO {
    @NotBlank(message = "Playlist name is mandatory.")
    @Pattern(
            regexp = "^$|^(?!.*[\\\\{}^`\\[\\]<>|])\\S(?:.*\\S)?$",
            message = "Playlist name cannot start or end with whitespace or contain \\, {, }, ^, `, [, ], <, >, or |."
    )
    @Size(min = 3, max = 50, message = "Playlist name must be 3–50 characters long.")
    private String name;

    @NotNull(message = "Visibility is mandatory.")
    @Builder.Default
    private VisibilityType visibility = VisibilityType.PUBLIC;

    @Builder.Default
    @JsonSetter(nulls = Nulls.SKIP)
    private List<UUID> songIds = new ArrayList<>();
}
