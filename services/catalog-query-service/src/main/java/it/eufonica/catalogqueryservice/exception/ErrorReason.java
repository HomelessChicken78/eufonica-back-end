package it.eufonica.catalogqueryservice.exception;

public enum ErrorReason {
    // 4XX
    // 400 Equivalent
    // Pages
    VALIDATION_ERROR,
    INVALID_PAGINATION_PARAMETER,

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
