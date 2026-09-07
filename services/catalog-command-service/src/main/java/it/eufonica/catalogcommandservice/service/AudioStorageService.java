package it.eufonica.catalogcommandservice.service;

import it.eufonica.catalogcommandservice.dto.song.AudioMetadataDTO;
import org.apache.tika.mime.MimeType;
import org.springframework.web.multipart.MultipartFile;

public interface AudioStorageService {
    /**
     * Stores the given audio file and returns its URL.
     *
     * @param file the audio file to store
     * @param audioMetadata the file's metadata, containing the extension and the true mime type
     * @return the S3 Object Key of the stored audio file
     */
    String store(MultipartFile file, AudioMetadataDTO audioMetadata);
}
