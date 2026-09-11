package it.eufonica.catalogcommandservice.service.media;

import it.eufonica.catalogcommandservice.dto.song.AudioMetadataDTO;
import it.eufonica.catalogcommandservice.exception.client.BadRequestException;
import it.eufonica.catalogcommandservice.exception.client.ContentTooLargeException;
import org.springframework.web.multipart.MultipartFile;

public interface AudioMetadataService {
    /**
     * Validates the metadata of the file:
     *
     * <p>Check that the file is present and not empty, and that its size does not exceed the configured
     * maximum audio file size.</p>
     *
     * @param file the audio file to validate
     * @throws BadRequestException if the file is null, empty, or the mime type is not allowed
     * @throws ContentTooLargeException if the file exceeds the configured maximum file size
     *
     * @return AudioMetadataDTO containing the detected mime type and the extension name
     */
    AudioMetadataDTO validate(MultipartFile file);

    /**
     * Get the duration in seconds of the audio.
     *
     * @param file the audio file to get the duration of
     * @param audioExtension the file's extension
     */
    int getDurationSec(MultipartFile file, String audioExtension);
}
