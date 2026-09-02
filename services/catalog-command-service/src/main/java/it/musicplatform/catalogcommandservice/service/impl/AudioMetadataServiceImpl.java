package it.musicplatform.catalogcommandservice.service.impl;

import it.musicplatform.catalogcommandservice.exception.client.BadRequestException;
import it.musicplatform.catalogcommandservice.exception.client.ContentTooLargeException;
import it.musicplatform.catalogcommandservice.exception.server.InternalServerErrorException;
import it.musicplatform.catalogcommandservice.service.AudioMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class AudioMetadataServiceImpl implements AudioMetadataService {
    private final Tika tika;

    @Value("#{'${MUSIC_FORMATS:audio/mpeg,audio/wav}'.split(',')}")
    private List<String> musicFormats;

    @Value("${MAX_AUDIO_SIZE}")
    private DataSize maxAudioSize;

    /**
     * Validates that the given MIME type is included in the list of allowed MIME types.
     *
     * @param mimeType the MIME type to validate
     * @param allowedExtensions the list of allowed MIME types, for example
     *                          {@code ["audio/mpeg", "audio/wav"]}
     * @throws BadRequestException if the MIME type is not allowed
     */
    private void validateMimeType(MimeType mimeType, List<String> allowedExtensions) {
        if (mimeType == null || allowedExtensions == null)
            throw new BadRequestException("MIME type and allowed MIME types must not be null");

        if (!allowedExtensions.contains(mimeType.toString())) {
            throw new BadRequestException(
                    "MIME type '" + mimeType + "' is not allowed. Allowed MIME types: "
                            + allowedExtensions);
        }
    }

    /**
     * Detects and validates the MIME type of the given audio file and returns
     * the corresponding file extension.
     *
     * @param file the audio file whose MIME type is to be detected and validated
     * @throws BadRequestException if the detected MIME type is not allowed
     * @throws InternalServerErrorException if the file cannot be read or the
     *         detected MIME type cannot be processed
     */
    private void detectAndValidateExtension(MultipartFile file) {
        try {
            // Detect the file MIME type
            String mimeTypeString = tika.detect(file.getInputStream());

            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType mimeType = allTypes.forName(mimeTypeString);

            // Check if the MIME type is allowed
            validateMimeType(mimeType, musicFormats);
        } catch (IOException | MimeTypeException e) {
            throw new InternalServerErrorException("Failed to process audio file format.");
        }
    }

    /**
     * Validates the metadata of the file:
     *
     * <p>Check that the file is present and not empty, and that its size does not exceed the configured
     * maximum audio file size.</p>
     *
     * @param file the audio file to validate
     * @throws BadRequestException() if the file is null or empty
     * @throws ContentTooLargeException if the file exceeds the configured maximum file size
     */
    private void validateFileMetadata(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("File is empty.");
            throw new BadRequestException("Audio file must not be empty.");
        }

        if (file.getSize() > maxAudioSize.toBytes()) {
            log.warn("File is too large. fileSize={} maxAllowedSize={}", file.getSize(),  maxAudioSize.toBytes());
            throw new ContentTooLargeException(
                    String.format("File size exceeds maximum limit of %s.", maxAudioSize.toString())
            );
        }
    }

    @Override
    public void validate(MultipartFile file) {
        detectAndValidateExtension(file);
        validateFileMetadata(file);
    }

    @Override
    public int getDurationSec(MultipartFile file) {
        return 0;
    }
}
