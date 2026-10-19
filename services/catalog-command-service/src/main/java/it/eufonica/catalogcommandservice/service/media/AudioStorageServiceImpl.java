package it.eufonica.catalogcommandservice.service.media;

import it.eufonica.catalogcommandservice.dto.song.AudioMetadataDTO;
import it.eufonica.exception.server.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import java.io.IOException;
import java.util.UUID;

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
    public void store(MultipartFile file, String objectKey, AudioMetadataDTO audioMetadata) {
        if (objectKey.endsWith("/"))
            throw new IllegalArgumentException("Object key shouldn't end with '/'");

        try {
            log.info("Storing audio file: originalFilename={}, extension={}, size={}",
                    file.getOriginalFilename(), audioMetadata.getExtension(), file.getSize());

            byte[] fileBytes = file.getBytes();
            log.debug("Uploading audio file to S3: bucket={}, key={}", bucketS3, objectKey);

            if (s3UploadEnabled) {
                s3Client.putObject(
                        b -> b.bucket(bucketS3)
                                .key(objectKey)
                                .contentType(audioMetadata.getMimeType().toString())
                                .build(),
                        AsyncRequestBody.fromBytes(fileBytes)
                ).join();

                log.info("Successfully stored audio file: key={}", objectKey);
            } else log.warn("S3 Upload is disabled.");
        } catch (IOException e) {
            throw new InternalServerErrorException(
                    "Failed to read bytes of uploaded audio file. originalFilename=" + file.getOriginalFilename(), e);
        }
    }

    @Override
    public String generateObjectKey(String prefix, String fileName, AudioMetadataDTO audioMetadata) {
        if (!prefix.endsWith("/"))
            throw new IllegalArgumentException("Prefix must end with '/'");

        String objectKey = audioPrefix + prefix
                + UUID.randomUUID() + "_"
                + StringUtils.cleanPath(fileName)
                + audioMetadata.getExtension();
        log.debug("Generated object key: objectKey={}", objectKey);
        return objectKey;
    }
}
