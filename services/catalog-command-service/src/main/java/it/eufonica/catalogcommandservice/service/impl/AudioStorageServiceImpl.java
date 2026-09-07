package it.eufonica.catalogcommandservice.service.impl;

import it.eufonica.catalogcommandservice.dto.song.AudioMetadataDTO;
import it.eufonica.catalogcommandservice.exception.server.InternalServerErrorException;
import it.eufonica.catalogcommandservice.service.AudioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.IOException;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class AudioStorageServiceImpl implements AudioStorageService {
    private final S3AsyncClient s3Client;

    @Value("${S3_BUCKET_NAME}")
    private String bucketS3;

    @Value("${S3_AUDIO_PREFIX:audio/}")
    private String audioPrefix;

    @Value("${S3_UPLOAD_ENABLED:false}")
    private boolean s3UploadEnabled;

    @Override
    public String store(MultipartFile file, AudioMetadataDTO audioMetadata) {
        try {
            log.info("Storing audio file: originalFilename={}, extension={}, size={}",
                    file.getOriginalFilename(), audioMetadata.getExtension(), file.getSize());

            byte[] fileBytes = file.getBytes();

            final String s3ObjectKey = audioPrefix + file.getOriginalFilename() + audioMetadata.getExtension();
            log.debug("Uploading audio file to S3: bucket={}, key={}", bucketS3, s3ObjectKey);

            if (s3UploadEnabled) {
                s3Client.putObject(
                        b -> b.bucket(bucketS3)
                                .key(s3ObjectKey)
                                .contentType(audioMetadata.getMimeType().toString())
                                .build(),
                        AsyncRequestBody.fromBytes(fileBytes)
                ).join();

                log.info("Successfully stored audio file: key={}", s3ObjectKey);
            } else log.warn("S3 Upload is disabled.");

            return s3ObjectKey;
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
