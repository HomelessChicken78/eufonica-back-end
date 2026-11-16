package it.eufonica.engagementservice.dto.listen;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Data
public class ListenCreateRequestDTO {
    @PositiveOrZero(message = "secPlayed must be greater or equal to zero.")
    @NotNull(message = "secPlayed is mandatory.")
    private Integer secPlayed;
}
