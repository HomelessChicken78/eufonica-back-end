package it.eufonica.catalogcommandservice.exception.client;

import it.eufonica.catalogcommandservice.exception.ErrorReason;

public class UnauthorizedException extends HttpClientErrorException {
    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }

    public UnauthorizedException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public UnauthorizedException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected UnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
