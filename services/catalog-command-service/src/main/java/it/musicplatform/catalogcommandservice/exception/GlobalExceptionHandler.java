package it.musicplatform.catalogcommandservice.exception;

import it.musicplatform.catalogcommandservice.exception.dto.GeneralErrorResponseDTO;
import it.musicplatform.catalogcommandservice.exception.dto.ValidationErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error400(BadRequestException err400) {
        log.warn("Bad request: {}", err400.getMessage());

        return ResponseEntity
                .badRequest()
                .body(new GeneralErrorResponseDTO(err400.getMessage(), 400));
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error401(UnauthorizedException err401) {
        log.warn("Unauthorized request: {}", err401.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new GeneralErrorResponseDTO(err401.getMessage(), 401));
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error402(PaymentRequiredException err402) {
        log.warn("Payment required: {}", err402.getMessage());

        return ResponseEntity
                .status(HttpStatus.PAYMENT_REQUIRED)
                .body(new GeneralErrorResponseDTO(err402.getMessage(), 402));
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error403(ForbiddenException err403) {
        log.warn("Forbidden request: {}", err403.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new GeneralErrorResponseDTO(err403.getMessage(), 403));
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error404Handler(NotFoundException err404) {
        log.warn("Resource not found: {}", err404.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new GeneralErrorResponseDTO(err404.getMessage(), 404));
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error409(ConflictException err409) {
        log.warn("Conflict: {}", err409.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new GeneralErrorResponseDTO(err409.getMessage(), 409));
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error413(ContentTooLargeException err413) {
        log.warn("Content too large: {}", err413.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONTENT_TOO_LARGE)
                .body(new GeneralErrorResponseDTO(err413.getMessage(), 413));
    }

    @ExceptionHandler
    public ResponseEntity<ValidationErrorResponseDTO> errorValidationHandler(
            MethodArgumentNotValidException exceptionRaised) {

        log.warn("Request validation failed: {} validation errors",
                exceptionRaised.getFieldErrorCount());

        ValidationErrorResponseDTO responseDTO = new ValidationErrorResponseDTO(
                exceptionRaised.getFieldErrors()
                        .stream()
                        .collect(Collectors.toMap(
                                FieldError::getField,
                                err -> err.getDefaultMessage() != null
                                        ? err.getDefaultMessage()
                                        : "missing error message",
                                (existing, replacement) -> existing
                        ))
        );

        return ResponseEntity
                .badRequest()
                .body(responseDTO);
    }
}
