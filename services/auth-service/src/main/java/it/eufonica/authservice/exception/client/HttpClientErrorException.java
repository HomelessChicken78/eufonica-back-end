package it.eufonica.authservice.exception.client;

import it.eufonica.authservice.exception.ErrorReason;
import it.eufonica.authservice.exception.HttpException;

public class HttpClientErrorException extends HttpException {
    public HttpClientErrorException() {
        super();
    }

    public HttpClientErrorException(String message) {
        super(message);
    }

    public HttpClientErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientErrorException(Throwable cause) {
        super(cause);
    }

    public HttpClientErrorException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public HttpClientErrorException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected HttpClientErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
