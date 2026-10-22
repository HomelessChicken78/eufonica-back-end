package it.eufonica.catalogcommandservice.exception;

import it.eufonica.exception.ErrorReason;

public enum CatalogErrorReason implements ErrorReason {
    // 4XX
    // 400 Equivalent
    MISSING_REQUIRED_FIELD,

    // MIME & Files
    MIME_NOT_ALLOWED,
    MISSING_MIME_TYPE,
    MISSING_AUDIO_FILE,

    // 403 Equivalent
    NOT_AN_ARTIST,
    ARTIST_OWNERSHIP_MISMATCH,
    NOT_RESOURCE_OWNER,

    // 409 Equivalent
    // Artists
    ARTIST_NAME_ALREADY_EXISTS,
    ARTIST_FOUNDED_AFTER_ALBUM_RELEASE,
    ARTIST_REGISTERED_AFTER_ALBUM_PUBLICATION,
    ARTIST_FOUNDED_AFTER_ALBUM_PUBLICATION,
    ALBUM_MISSING_COMMON_CREDITED_ARTIST,

    // Song
    SONG_NAME_ALREADY_EXISTS_FOR_ARTIST,

    // Song-Artist (song_credit)
    CANNOT_REMOVE_PRIMARY_OWNER_CREDIT,
    INVALID_CREDITED_ARTIST_FOUNDATION_DATE,

    // Album
    ALBUM_RELEASE_DATE_AFTER_PUBLICATION_DATE,

    // Album-Song (album_contains)
    SONG_NOT_IN_ALBUM,
    ALBUM_EMPTY_AFTER_SONG_DELETION,
    ALBUM_EMPTY_AFTER_SONG_REMOVED,

    // 413 Equivalent
    AUDIO_FILE_TOO_LARGE,

    // 5XX
    // 500 Equivalent
    UNREADABLE_AUDIO_FILE,
}
