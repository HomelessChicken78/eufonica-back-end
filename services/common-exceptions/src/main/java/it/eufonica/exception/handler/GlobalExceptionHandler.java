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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private ResponseEntity<GeneralErrorResponseDTO> buildErrorResponse(ErrorReason reason, String message, HttpStatus status) {
        GeneralErrorResponseDTO body = new GeneralErrorResponseDTO(message, status.value(), reason);
        return ResponseEntity.status(status).body(body);
    }

    private ResponseEntity<GeneralErrorResponseDTO> buildErrorResponse(HttpException e, HttpStatus status) {
        return buildErrorResponse(e.getReason(), e.getMessage(), status);
    }

    /**
     * Build an {@code ErrorReason} from a {@code HttpStatusCode} by parsing its reason phrase.
     * If the status code is not recognized or the enum does not have an equivalent constant,
     * this returns {@code CommonErrorReason.INTERNAL_ERROR} for 5xx and {@code CommonErrorReason.REQUEST_ERROR}
     * for any other HTTP Status Code Classifier (1xx, 2xx, 3xx and 4xx).
     *
     * @param statusCode The specific status code to parse into an {@code ErrorReason}
     *
     * @return The specific ErrorReason corresponding to the {@code statusCode}'s reason phrase or, if
     * not found, {@code CommonErrorReason.INTERNAL_ERROR}/{@code CommonErrorReason.REQUEST_ERROR}
     */
    private ErrorReason resolveByStatusName(HttpStatusCode statusCode) {
        try {
            return CommonErrorReason.valueOf(HttpStatus.valueOf(statusCode.value()).name());
        } catch (IllegalArgumentException e) {
            // Fallback in case either the http status code is invalid (e.g. 955) or the enum
            // doesn't still have a specific reason for that yet
            return statusCode.is5xxServerError()
                    ? CommonErrorReason.INTERNAL_ERROR
                    : CommonErrorReason.REQUEST_ERROR;
        }
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

    @Override @NullMarked
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exceptionRaised, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

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

    @Override @NullMarked
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ErrorReason reason;
        // Ambiguous http status codes: more reason lead to the same status code.
        // We dispatch the reason based on the type of exception
        if (statusCode.value() == 400) {
            if (ex instanceof HttpMessageNotReadableException || ex instanceof TypeMismatchException)
                reason = CommonErrorReason.MALFORMED_REQUEST;
            else if (ex instanceof MissingServletRequestParameterException)
                reason = CommonErrorReason.MISSING_REQUEST_PARAM;
            else if (ex instanceof BindException)
                reason = CommonErrorReason.VALIDATION_ERROR;
            else
                reason = CommonErrorReason.MALFORMED_REQUEST;

        // Distinguish 404 for an endpoint not found from a 404 for a resource not found
        } else if (statusCode.value() == 404)
            reason = CommonErrorReason.ENDPOINT_NOT_FOUND;

        // For any other exception, build the reason from the status code's reason phrase.
        // This is because the rest of the status codes are usually self documenting
        else
            reason = resolveByStatusName(statusCode);

        log.warn("Request handling failed: {}", ex.getMessage());

        GeneralErrorResponseDTO dto = new GeneralErrorResponseDTO(
                "An error occurred while processing the request.",
                statusCode.value(),
                reason
        );

        return ResponseEntity.status(statusCode).headers(headers).body(dto);
    }
}
