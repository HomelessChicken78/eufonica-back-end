package it.eufonica.catalogcommandservice.service.impl;

import it.eufonica.catalogcommandservice.service.AudioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class AudioStorageServiceImpl implements AudioStorageService {
    private final S3AsyncClient s3AsyncClient;

    @Value("${S3_BUCKET_NAME}")
    private String bucketS3;

    @Value("${S3_AUDIO_PREFIX:audio/}")
    private String audioPrefix;

    @Override
    public String store(MultipartFile file) {
        // s3AsyncClient.putObject()

        return "photos/2026/09/vacation.jpg";
    }
}
