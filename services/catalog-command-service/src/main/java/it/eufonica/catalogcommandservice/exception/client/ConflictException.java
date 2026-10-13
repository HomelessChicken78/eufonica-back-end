package it.eufonica.catalogcommandservice.exception.client;

import it.eufonica.catalogcommandservice.exception.ErrorReason;

public class ConflictException extends HttpClientErrorException {
    public ConflictException() {
        super();
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictException(Throwable cause) {
        super(cause);
    }

    public ConflictException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public ConflictException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected ConflictException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
