package it.eufonica.exception;

public enum CommonErrorReason implements ErrorReason {
    // 4XX
    // 400 Equivalent
    VALIDATION_ERROR,

    // 409 Equivalent
    // Optimistic Locking
    OPTIMISTIC_LOCKING_FAILURE,

    // 5XX
    // 500 Equivalent
    INTERNAL_ERROR,

    // Misc
    UNEXPECTED_ERROR
}
