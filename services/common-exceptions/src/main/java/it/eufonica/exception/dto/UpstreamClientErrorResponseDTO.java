package it.eufonica.exception.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpstreamClientErrorResponseDTO {
    private String message;

    @EqualsAndHashCode.Exclude
    private LocalDateTime timestamp;

    private int status = 400;

    private String reason;
}
