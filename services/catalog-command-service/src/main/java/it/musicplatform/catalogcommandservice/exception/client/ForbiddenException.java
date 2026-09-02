package it.musicplatform.catalogcommandservice.exception.client;

public class ForbiddenException extends HttpClientErrorException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    protected ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
