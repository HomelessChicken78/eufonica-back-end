package it.eufonica.exception.server;

import it.eufonica.exception.ErrorReason;

public class NotImplementedException extends HttpServerErrorException  {
    public NotImplementedException() {
        super();
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }

    public NotImplementedException(String message, ErrorReason reason) {
        super(message, reason);
    }

    protected NotImplementedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotImplementedException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }
}
