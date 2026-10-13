package it.eufonica.authservice.exception.server;

import it.eufonica.authservice.exception.ErrorReason;
import it.eufonica.authservice.exception.HttpException;

public class HttpServerErrorException extends HttpException {
    public HttpServerErrorException() {
        super();
    }

    public HttpServerErrorException(String message) {
        super(message);
    }

    public HttpServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpServerErrorException(Throwable cause) {
        super(cause);
    }

    public HttpServerErrorException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public HttpServerErrorException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected HttpServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
