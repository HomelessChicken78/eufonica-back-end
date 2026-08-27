package it.musicplatform.catalogcommandservice.exception;

public class PaymentRequiredException extends HttpClientErrorException {
    public PaymentRequiredException() {
        super();
    }

    public PaymentRequiredException(String message) {
        super(message);
    }

    public PaymentRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentRequiredException(Throwable cause) {
        super(cause);
    }

    protected PaymentRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
