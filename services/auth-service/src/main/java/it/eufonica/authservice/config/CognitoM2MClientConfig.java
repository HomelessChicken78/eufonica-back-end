package it.eufonica.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CognitoM2MClientConfig {
    @Value("${COGNITO_DOMAIN}")
    private String cognitoDomain;

    @Bean
    public RestClient cognitoTokenRestClient() {
        return RestClient.builder()
                .baseUrl(cognitoDomain)
                .build();
    }
}