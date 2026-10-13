package it.eufonica.authservice.exception.client;

import it.eufonica.authservice.exception.ErrorReason;

public class ContentTooLargeException extends HttpClientErrorException {
    public ContentTooLargeException() {
        super();
    }

    public ContentTooLargeException(String message) {
        super(message);
    }

    public ContentTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentTooLargeException(Throwable cause) {
        super(cause);
    }

    public ContentTooLargeException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public ContentTooLargeException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected ContentTooLargeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
