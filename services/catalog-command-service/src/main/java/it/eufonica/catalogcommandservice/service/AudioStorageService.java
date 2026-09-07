package it.eufonica.catalogcommandservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface AudioStorageService {
    /**
     * Stores the given audio file and returns its URL.
     *
     * @param file the audio file to store
     * @return the Object Keys of the stored audio file
     */
    String store(MultipartFile file);
}
