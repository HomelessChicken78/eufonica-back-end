package it.eufonica.authservice.model;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter @Builder
@ToString @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AppUser {
    @EqualsAndHashCode.Include
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "\\w*",
        message = "Display name must only contain letters, numbers or underscores.")
    @Size.List({
            @Size(min = 4, message = "Display name must be at least 4 characters."),
            @Size(max = 20, message = "Display name must not exceed 20 characters.")
    })
    private String displayName;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

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

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime registrationTimestamp;
}
