package it.eufonica.catalogcommandservice.exception;

import it.eufonica.catalogcommandservice.model.Album;
import it.eufonica.exception.ErrorReason;
import it.eufonica.exception.client.ConflictException;
import lombok.Getter;

import java.util.Set;

public class AlbumEmptyAfterSongDeletionException extends ConflictException {
    @Getter
    private final Set<Album> emptiedAlbums;

    public AlbumEmptyAfterSongDeletionException(Set<Album> emptiedAlbums) {
        super();
        this.emptiedAlbums = emptiedAlbums;
    }

    public AlbumEmptyAfterSongDeletionException(String message, Set<Album> emptiedAlbums) {
        super(message);
        this.emptiedAlbums = emptiedAlbums;
    }

    public AlbumEmptyAfterSongDeletionException(Throwable cause, Set<Album> emptiedAlbums) {
        super(cause);
        this.emptiedAlbums = emptiedAlbums;
    }

    public AlbumEmptyAfterSongDeletionException(String message, Throwable cause, Set<Album> emptiedAlbums) {
        super(message, cause);
        this.emptiedAlbums = emptiedAlbums;
    }

    protected AlbumEmptyAfterSongDeletionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Set<Album> emptiedAlbums) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.emptiedAlbums = emptiedAlbums;
    }
}
