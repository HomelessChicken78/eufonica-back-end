package it.eufonica.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequestDTO {
    @Pattern(regexp = "\\w*",
            message = "Display name must only contain letters, numbers or underscores.")
    @Size.List({
            @Size(min = 4, message = "Display name must be at least 4 characters."),
            @Size(max = 20, message = "Display name must not exceed 20 characters.")
    })
    @NotBlank(message = "Display name is mandatory.")
    private String displayName;

    @Pattern(regexp = "\\p{L}*", message = "First name must contain only letters.")
    @Size.List({
            @Size(min = 2, message = "First name must be at least 2 characters."),
            @Size(max = 20, message = "First name must not exceed 20 characters.")
    })
    private String firstName;

    @Pattern(regexp = "\\p{L}*", message = "Middle name must contain only letters.")
    @Size.List({
            @Size(min = 3, message = "Middle name must be at least 3 characters."),
            @Size(max = 20, message = "Middle name must not exceed 20 characters.")
    })
    private String middleName;

    @Pattern(regexp = "\\p{L}*", message = "Last name must contain only letters.")
    @Size.List({
            @Size(min = 3, message = "Last name must be at least 3 characters."),
            @Size(max = 20, message = "Last name must not exceed 20 characters.")
    })
    private String lastName;
}
