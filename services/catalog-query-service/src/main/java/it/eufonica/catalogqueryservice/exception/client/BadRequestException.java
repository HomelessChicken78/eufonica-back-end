package it.eufonica.catalogqueryservice.exception.client;

import it.eufonica.catalogqueryservice.exception.ErrorReason;

public class BadRequestException extends HttpClientErrorException {
    public BadRequestException() {
        super();
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadRequestException(Throwable cause) {
        super(cause);
    }

    public BadRequestException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public BadRequestException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
