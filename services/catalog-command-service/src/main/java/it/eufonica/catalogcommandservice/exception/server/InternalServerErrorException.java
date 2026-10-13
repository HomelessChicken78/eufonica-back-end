package it.eufonica.catalogcommandservice.exception.server;

import it.eufonica.catalogcommandservice.exception.ErrorReason;

public class InternalServerErrorException extends HttpServerErrorException {
    public InternalServerErrorException() {
        super();
    }

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }

    public InternalServerErrorException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public InternalServerErrorException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected InternalServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
