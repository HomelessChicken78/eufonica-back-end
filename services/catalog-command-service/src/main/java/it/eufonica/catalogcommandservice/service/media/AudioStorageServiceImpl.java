package it.eufonica.catalogcommandservice.service.media;

import it.eufonica.catalogcommandservice.dto.song.AudioMetadataDTO;
import it.eufonica.catalogcommandservice.exception.server.InternalServerErrorException;
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
    public String store(MultipartFile file, String prefix, String fileName, AudioMetadataDTO audioMetadata) {
        if (!prefix.endsWith("/"))
            throw new IllegalArgumentException("Prefix must end with '/'");

        try {
            log.info("Storing audio file: originalFilename={}, extension={}, size={}",
                    file.getOriginalFilename(), audioMetadata.getExtension(), file.getSize());

            byte[] fileBytes = file.getBytes();

            // Use a UUID to ensure unique S3 object keys, as different filenames such as "a/b"
            // and "a b" may be normalized to the same key and otherwise overwrite each other.
            // cleanPath() normalizes the filename/path before storing it in S3
            final String s3ObjectKey = audioPrefix + prefix
                    + UUID.randomUUID() + "_"
                    + StringUtils.cleanPath(fileName)
                    + audioMetadata.getExtension();
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
