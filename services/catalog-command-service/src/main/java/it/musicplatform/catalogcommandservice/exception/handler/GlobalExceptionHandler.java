package it.musicplatform.catalogcommandservice.exception.handler;

import it.musicplatform.catalogcommandservice.exception.server.InternalServerErrorException;
import it.musicplatform.catalogcommandservice.exception.client.*;
import it.musicplatform.catalogcommandservice.exception.dto.GeneralErrorResponseDTO;
import it.musicplatform.catalogcommandservice.exception.dto.ValidationErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Optional;
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

        var errors = exceptionRaised.getFieldErrors();

        // Convert the single field errors into a map to use for the error response and the logging
        Map<String, String> validationErrors = errors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage())
                                .orElse("missing error message"),
                        (existing, replacement) -> existing
                ));

        // Log the single errors
        log.warn("Request validation failed ({} error(s)):\n{}",
                validationErrors.size(),
                validationErrors.entrySet().stream() // Maps can't be .stream, so we take the entry set
                        .map(entry -> "  - %s: %s".formatted(entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining("\n"))
        );

        return ResponseEntity
                .badRequest()
                .body(new ValidationErrorResponseDTO(validationErrors));
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error500(InternalServerErrorException err500) {
        log.error("Unexpected error while processing request", err500);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GeneralErrorResponseDTO(
                        "An unexpected error occurred",
                        500
                ));
    }
}
