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
            regexp = "^[A-Za-z0-9_][A-Za-z0-9_ ]{1,18}[A-Za-z0-9_]$",
            message = "Name must be 3–20 characters long, cannot start or end with a space, " +
                    "and may contain only letters, numbers, underscores, or spaces."
    )
    private String name;

    @NotNull(message = "Foundation date is mandatory.")
    @PastOrPresent(message = "Foundation date can't be in the future.")
    private LocalDate foundationDate;
}
