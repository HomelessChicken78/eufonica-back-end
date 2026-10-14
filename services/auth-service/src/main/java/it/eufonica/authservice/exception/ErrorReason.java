package it.eufonica.authservice.exception;

public enum ErrorReason {
    // 4XX
    // 400 Equivalent
    // Pages
    VALIDATION_ERROR,
    INVALID_PAGINATION_PARAMETER,

    // 401 Equivalent
    UNAUTHENTICATED,

    // 403 Equivalent
    NOT_AN_USER,
    NOT_AN_ADMIN,

    // 404 Equivalent
    RESOURCE_NOT_FOUND,

    // 409 Equivalent
    // Optimistic Locking
    OPTIMISTIC_LOCKING_FAILURE,

    // Signup
    RESERVED_DISPLAY_NAME,
    ALREADY_SIGNED_UP,
    DISPLAY_NAME_ALREADY_IN_USE,

    // Artist Request
    ALREADY_AN_ARTIST,
    ARTIST_REQUEST_ALREADY_PENDING,
    ARTIST_REGISTERED_AFTER_REQUEST,
    REQUEST_ALREADY_EVALUATED,
    SELF_EVALUATION_NOT_ALLOWED,

    // 5XX
    // 500 Equivalent
    INTERNAL_ERROR,

    // Misc
    UNEXPECTED_ERROR
}
