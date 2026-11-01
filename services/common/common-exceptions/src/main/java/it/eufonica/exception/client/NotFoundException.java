package it.eufonica.exception.client;

import it.eufonica.exception.ErrorReason;

public class NotFoundException extends HttpClientErrorException {
    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public NotFoundException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
