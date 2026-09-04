package it.eufonica.catalogcommandservice.exception.server;

import it.eufonica.catalogcommandservice.exception.HttpException;

public class HttpServerErrorException extends HttpException {
    public HttpServerErrorException(String message) {
        super(message);
    }

    public HttpServerErrorException() {
        super();
    }

    public HttpServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpServerErrorException(Throwable cause) {
        super(cause);
    }

    protected HttpServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
