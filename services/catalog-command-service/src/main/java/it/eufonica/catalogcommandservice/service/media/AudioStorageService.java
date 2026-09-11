package it.eufonica.catalogcommandservice.service.media;

import it.eufonica.catalogcommandservice.dto.song.AudioMetadataDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AudioStorageService {
    /**
     * Stores the given audio file and returns its URL.
     *
     * @param file the audio file to store
     * @param audioMetadata the file's metadata, containing the extension and the true mime type
     * @param prefix the prefix of the file to upload on s3
     * @param fileName the name of the file to store
     * @return the S3 Object Key of the stored audio file
     *
     * @throws IllegalArgumentException If the prefix doesn't end with '/'
     */
    String store(MultipartFile file, String prefix, String fileName, AudioMetadataDTO audioMetadata);
}
