package it.eufonica.catalogcommandservice.exception.client;

import it.eufonica.catalogcommandservice.exception.ErrorReason;

public class ForbiddenException extends HttpClientErrorException {
    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    public ForbiddenException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public ForbiddenException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
