package it.eufonica.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.eufonica.exception.ErrorReason;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralErrorResponseDTO {
    private String message;

    private final LocalDateTime timestamp = LocalDateTime.now();

    private int status = 400;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorReason reason;

    public GeneralErrorResponseDTO(String message, ErrorReason reason) {
        this.message = message;
        this.reason = reason;
    }

    public GeneralErrorResponseDTO(String message, int status, ErrorReason reason) {
        this.message = message;
        this.status = status;
        this.reason = reason;
    }
}