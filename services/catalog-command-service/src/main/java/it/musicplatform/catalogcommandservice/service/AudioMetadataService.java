package it.musicplatform.catalogcommandservice.service;

import it.musicplatform.catalogcommandservice.exception.client.BadRequestException;
import it.musicplatform.catalogcommandservice.exception.client.ContentTooLargeException;
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
     */
    void validate(MultipartFile file);

    /**
     * Get the duration in seconds of the audio.
     *
     * @param file the audio file to get the duration of
     */
    int getDurationSec(MultipartFile file);
}
