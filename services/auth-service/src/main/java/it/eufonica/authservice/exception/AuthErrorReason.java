package it.eufonica.authservice.exception;

import it.eufonica.exception.ErrorReason;

public enum AuthErrorReason implements ErrorReason {
    // 4XX
    // 409 Equivalent
    // Signup
    RESERVED_DISPLAY_NAME,
    ALREADY_SIGNED_UP,
    DISPLAY_NAME_ALREADY_IN_USE,

    // Artist Request
    ALREADY_AN_ARTIST,
    ARTIST_REQUEST_ALREADY_PENDING,
    ARTIST_REGISTERED_AFTER_REQUEST,
    REQUEST_ALREADY_EVALUATED,
    SELF_EVALUATION_NOT_ALLOWED
}
