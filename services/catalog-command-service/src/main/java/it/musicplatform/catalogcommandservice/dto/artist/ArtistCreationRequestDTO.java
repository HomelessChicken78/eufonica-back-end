package it.musicplatform.catalogcommandservice.dto.artist;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@ToString
public class ArtistCreationRequestDTO {
    @NotBlank(message = "Artist's name can't be empty")
    @Pattern(
            regexp = "^[A-Za-z0-9_]{3,20}$",
            message = "Name must be 3–20 characters long and contain only letters, numbers, or underscores"
    )
    private String name;

    @NotNull(message = "Foundation date is mandatory.")
    @PastOrPresent(message = "Foundation date can't be in the future.")
    private LocalDate foundationDate;
}
