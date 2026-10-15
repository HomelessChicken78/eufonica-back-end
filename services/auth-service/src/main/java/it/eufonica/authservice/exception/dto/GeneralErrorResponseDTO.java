package it.eufonica.authservice.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.eufonica.authservice.exception.ErrorReason;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralErrorResponseDTO {
    private String message;

    private LocalDateTime timestamp = LocalDateTime.now();

    private Integer status = 400;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ErrorReason reason;

    public GeneralErrorResponseDTO(String message, ErrorReason reason) {
        this.message = message;
    }

    public GeneralErrorResponseDTO(String message, Integer status, ErrorReason reason) {
        this.message = message;
        this.status = status;
    }
}