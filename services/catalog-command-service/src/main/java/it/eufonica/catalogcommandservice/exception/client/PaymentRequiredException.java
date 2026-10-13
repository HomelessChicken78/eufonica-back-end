package it.eufonica.catalogcommandservice.exception.client;

import it.eufonica.catalogcommandservice.exception.ErrorReason;

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

    public PaymentRequiredException(String message, ErrorReason reason) {
        super(message, reason);
    }

    protected PaymentRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PaymentRequiredException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }
}
