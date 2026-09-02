package it.musicplatform.catalogcommandservice.exception.http.server;

public class InternalServerErrorException extends HttpServerErrorException {
    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException() {
        super();
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerErrorException(Throwable cause) {
        super(cause);
    }

    protected InternalServerErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
