package it.eufonica.catalogcommandservice.service.media;

import it.eufonica.catalogcommandservice.dto.song.AudioMetadataDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AudioStorageService {
    /**
     * Stores the given audio file and returns its URL.
     *
     * @param file the audio file to store
     * @param objectKey The object key the file should be stored with. Must end with '/'
     * @param audioMetadata the file's metadata, containing the extension and the true mime type
     *
     * @throws IllegalArgumentException If the prefix doesn't end with '/'
     */
    void store(MultipartFile file, String objectKey, AudioMetadataDTO audioMetadata);

    /**
     * Generates the S3 object key under which an audio file would be stored,
     * without performing any upload.
     *
     * @param prefix the prefix under which the object key is generated. Must end with '/'
     * @param fileName the name of the file the key is generated for
     * @param audioMetadata the file's metadata, containing the extension and the true mime type
     *
     * @return the generated S3 Object Key
     * @throws IllegalArgumentException If the prefix doesn't end with '/'
     */
    String generateObjectKey(String prefix, String fileName, AudioMetadataDTO audioMetadata);
}
