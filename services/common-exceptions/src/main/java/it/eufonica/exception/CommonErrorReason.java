package it.eufonica.exception;

public enum CommonErrorReason implements ErrorReason {
    // 4XX
    // 400 Equivalent
    VALIDATION_ERROR,

    // Pages
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

    // 5XX
    // 500 Equivalent
    INTERNAL_ERROR,

    // Misc
    UNEXPECTED_ERROR
}
