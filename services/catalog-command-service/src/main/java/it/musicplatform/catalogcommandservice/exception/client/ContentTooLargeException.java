package it.musicplatform.catalogcommandservice.exception.client;

public class ContentTooLargeException extends HttpClientErrorException {
    public ContentTooLargeException(String message) {
        super(message);
    }

    public ContentTooLargeException() {
        super();
    }

    public ContentTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentTooLargeException(Throwable cause) {
        super(cause);
    }

    protected ContentTooLargeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
