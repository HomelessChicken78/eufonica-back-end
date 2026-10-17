package it.eufonica.catalogcommandservice.exception;

import it.eufonica.exception.ErrorReason;
import it.eufonica.exception.client.ConflictException;

public class AlbumEmptyAfterSongDeletionException extends ConflictException {
    public AlbumEmptyAfterSongDeletionException() {
        super();
    }

    public AlbumEmptyAfterSongDeletionException(String message) {
        super(message);
    }

    public AlbumEmptyAfterSongDeletionException(Throwable cause) {
        super(cause);
    }

    public AlbumEmptyAfterSongDeletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlbumEmptyAfterSongDeletionException(String message, ErrorReason reason) {
        super(message, reason);
    }

    public AlbumEmptyAfterSongDeletionException(String message, ErrorReason reason, Throwable cause) {
        super(message, reason, cause);
    }

    protected AlbumEmptyAfterSongDeletionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
