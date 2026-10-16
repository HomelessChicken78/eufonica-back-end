package it.eufonica.exception.handler;

import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.ErrorReason;
import it.eufonica.exception.HttpException;
import it.eufonica.exception.client.*;
import it.eufonica.exception.dto.GeneralErrorResponseDTO;
import it.eufonica.exception.dto.ValidationErrorResponseDTO;
import it.eufonica.exception.server.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
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
    private ResponseEntity<GeneralErrorResponseDTO> buildErrorResponse(ErrorReason reason, String message, HttpStatus status) {
        GeneralErrorResponseDTO body = new GeneralErrorResponseDTO(message, status.value(), reason);
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<GeneralErrorResponseDTO> buildErrorResponse(HttpException e, HttpStatus status) {
        return buildErrorResponse(e.getReason(), e.getMessage(), status);
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error400(BadRequestException err400) {
        log.warn("Bad request: {}", err400.getMessage());

        return buildErrorResponse(err400, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error401(UnauthorizedException err401) {
        log.warn("Unauthorized request: {}", err401.getMessage());

        return buildErrorResponse(err401, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error402(PaymentRequiredException err402) {
        log.warn("Payment required: {}", err402.getMessage());

        return buildErrorResponse(err402, HttpStatus.PAYMENT_REQUIRED);
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error403(ForbiddenException err403) {
        log.warn("Forbidden request: {}", err403.getMessage());

        return buildErrorResponse(err403, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error404Handler(NotFoundException err404) {
        log.warn("Resource not found: {}", err404.getMessage());

        return buildErrorResponse(err404, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error409(ConflictException err409) {
        log.warn("Conflict: {}", err409.getMessage());

        return buildErrorResponse(err409, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    ResponseEntity<GeneralErrorResponseDTO> handleOptimisticLockingFailure(
            OptimisticLockingFailureException exc,
            HttpServletRequest request
    ) {
        log.warn("Optimistic locking failure: method={}, uri={}, message={}",
                request.getMethod(), request.getRequestURI(), exc.getMessage());

        return buildErrorResponse(
                CommonErrorReason.OPTIMISTIC_LOCKING_FAILURE,
                "Resource was modified by another request. Please reload and try again.",
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error413(ContentTooLargeException err413) {
        log.warn("Content too large: {}", err413.getMessage());

        return buildErrorResponse(err413, HttpStatus.CONTENT_TOO_LARGE);
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
        log.error("Unexpected error while processing request.", err500);

        return buildErrorResponse(err500.getReason(), "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> error501(NotImplementedException err501) {
        log.error("NotImplemented: {}", err501.getMessage());

        return buildErrorResponse(err501, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler
    public ResponseEntity<GeneralErrorResponseDTO> errorUnexpected(Exception exc) {
        log.error("Unhandled exception while processing request.", exc);
        return buildErrorResponse(CommonErrorReason.UNEXPECTED_ERROR, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
