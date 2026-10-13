package it.eufonica.catalogqueryservice.exception;

import lombok.Getter;

@Getter
public class HttpException extends RuntimeException {
    private ErrorReason reason;

    public HttpException() {
        super();
    }

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public HttpException(String message, ErrorReason reason) {
        super(message);
        this.reason = reason;
    }

    public HttpException(String message, ErrorReason reason, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    protected HttpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
