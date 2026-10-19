package it.eufonica.catalogcommandservice.exception;

import it.eufonica.catalogcommandservice.mapper.AlbumMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice @RequiredArgsConstructor
public class SongAlbumExceptionHandler {
    private final AlbumMapper albumMapper;

    @ExceptionHandler
    public ResponseEntity<AlbumCascadeConflictResponseDTO> handleAlbumEmptyAfterSongDeletionException(AlbumEmptyAfterSongDeletionException exc) {
        AlbumCascadeConflictResponseDTO body = new AlbumCascadeConflictResponseDTO(
                exc.getMessage(),
                albumMapper.toRef(exc.getEmptiedAlbums())
        );

        return ResponseEntity.status(body.getStatus()).body(body);
    }
}
