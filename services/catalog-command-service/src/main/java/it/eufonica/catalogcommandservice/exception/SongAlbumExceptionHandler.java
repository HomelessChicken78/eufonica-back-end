package it.eufonica.catalogcommandservice.exception;

import it.eufonica.catalogcommandservice.mapper.AlbumMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor @Slf4j
public class SongAlbumExceptionHandler {
    private final AlbumMapper albumMapper;

    @ExceptionHandler
    public ResponseEntity<SongDeletionCascadeResponseDTO> handleAlbumEmptyAfterSongDeletionException(AlbumEmptyAfterSongDeletionException exc) {
        log.warn("Album cascade conflict on song deletion: {}", exc.getMessage());

        SongDeletionCascadeResponseDTO body = new SongDeletionCascadeResponseDTO(
                exc.getMessage(),
                albumMapper.toRef(exc.getEmptiedAlbums())
        );

        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @ExceptionHandler
    public ResponseEntity<SongRemovalCascadeResponseDTO> handleAlbumEmptyAfterSongRemovalException(AlbumEmptyAfterSongRemovalException exc) {
        log.warn("Album cascade conflict on song removal: {}", exc.getMessage());

        SongRemovalCascadeResponseDTO body = new SongRemovalCascadeResponseDTO(
                exc.getMessage(),
                albumMapper.toRef(exc.getEmptiedAlbum())
        );

        return ResponseEntity.status(body.getStatus()).body(body);
    }
}
