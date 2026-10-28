package it.eufonica.catalogcommandservice.exception;

import it.eufonica.catalogcommandservice.model.Album;
import it.eufonica.exception.client.ConflictException;
import lombok.Getter;

public class AlbumEmptyAfterSongRemovalException extends ConflictException {
    @Getter
    private final Album emptiedAlbum;

    public AlbumEmptyAfterSongRemovalException(Album emptiedAlbum) {
        super();
        this.emptiedAlbum = emptiedAlbum;
    }

    public AlbumEmptyAfterSongRemovalException(String message, Album emptiedAlbum) {
        super(message);
        this.emptiedAlbum = emptiedAlbum;
    }

    public AlbumEmptyAfterSongRemovalException(Throwable cause, Album emptiedAlbum) {
        super(cause);
        this.emptiedAlbum = emptiedAlbum;
    }

    public AlbumEmptyAfterSongRemovalException(String message, Throwable cause, Album emptiedAlbum) {
        super(message, cause);
        this.emptiedAlbum = emptiedAlbum;
    }

    protected AlbumEmptyAfterSongRemovalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Album emptiedAlbum) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.emptiedAlbum = emptiedAlbum;
    }
}
