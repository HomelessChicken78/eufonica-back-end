package it.eufonica.engagementservice.dto.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Data
public class PlaylistRenameRequestDTO {
    @NotBlank(message = "Playlist name is mandatory.")
    @Pattern(
            regexp = "^$|^(?!.*[\\\\{}^`\\[\\]<>|])\\S(?:.*\\S)?$",
            message = "Playlist name cannot start or end with whitespace or contain \\, {, }, ^, `, [, ], <, >, or |."
    )
    @Size(min = 3, max = 50, message = "Playlist name must be 3–50 characters long.")
    private String newName;
}
