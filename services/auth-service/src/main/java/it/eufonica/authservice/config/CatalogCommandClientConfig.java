package it.eufonica.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CatalogCommandClientConfig {
    @Value("${CATALOG_COMMAND_SERVICE_URL:http://localhost:8080}")
    private String catalogCommandServiceUrl;

    @Bean
    public RestClient catalogCommandRestClient() {
        return RestClient.builder()
                .baseUrl(catalogCommandServiceUrl)
                .build();
    }
}