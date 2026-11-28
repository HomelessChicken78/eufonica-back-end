package it.eufonica.catalogcommandservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;

import java.time.Duration;

@Configuration
public class S3Config {
    @Value("${AWS_REGION}")
    private String awsRegion;

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .region(Region.of(awsRegion))
                .overrideConfiguration(b -> b.apiCallTimeout(Duration.ofMinutes(2)))
                .build();
    }
}
