package it.musicplatform.catalogcommandservice.exception;

public class HttpClientErrorException extends HttpException {
    public HttpClientErrorException(String message) {
        super(message);
    }

    public HttpClientErrorException() {
        super();
    }

    public HttpClientErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpClientErrorException(Throwable cause) {
        super(cause);
    }

    protected HttpClientErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
