package it.eufonica.catalogcommandservice.dto.album;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
public class AlbumUpdateRequestDTO {
    @NotBlank(message = "Album name is required.")
    @Pattern(
            regexp = "^\\S(?:.*\\S)?$",
            message = "Album name cannot start or end with whitespace."
    )
    @Size(min = 1, max = 100, message = "Album name must be 1–100 characters long.")
    private String name;

    @NotNull(message = "Original release date is required.")
    @PastOrPresent(message = "Original release date cannot be in the future.")
    private LocalDate originalReleaseDate;

    private Set<@NotNull(message = "Song ID cannot be null.") UUID> songs;
}
