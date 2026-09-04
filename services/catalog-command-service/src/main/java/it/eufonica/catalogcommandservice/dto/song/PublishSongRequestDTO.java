package it.eufonica.catalogcommandservice.dto.song;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Data
public class PublishSongRequestDTO {
    @NotBlank(message = "Title is mandatory.")
    @Pattern(
            regexp = "^$|^\\S(?:.*\\S)?$",
            message = "Title cannot start or end with whitespace."
    )
    @Size(min = 3, max = 50, message = "Title must be 3–50 characters long.")
    private String title;

    @PastOrPresent(message = "Published date cannot be in the future.")
    private LocalDate publishedDate;

    @NotEmpty(message = "At least one credited artist is required.")
    private Set<@NotNull(message = "Artist ID cannot be null.") UUID> creditedArtists;
}
