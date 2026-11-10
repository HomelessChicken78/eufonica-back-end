package it.eufonica.engagementservice.exception;

import it.eufonica.exception.ErrorReason;

public enum EngagementErrorReason implements ErrorReason {
    // 4XX
    // 400 Equivalent
    INVALID_RANK_ORDER,

    // 403 Equivalent
    NOT_PLAYLIST_OWNER,

    // 404 Equivalent
    USER_NOT_YET_SYNCED
}
