package it.eufonica.authservice.security;

import it.eufonica.authservice.security.dto.CognitoTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service @Transactional
@RequiredArgsConstructor @Slf4j
public class CognitoM2MTokenServiceImpl implements CognitoM2MTokenService {
    private final RestClient cognitoTokenRestClient;

    @Value("${COGNITO_M2M_CLIENT_ID}")
    private String clientId;

    @Value("${COGNITO_M2M_CLIENT_SECRET}")
    private String clientSecret;

    @Value("${COGNITO_M2M_SCOPE}")
    private String scope;

    @Override
    public String getAccessToken() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", scope);

        var response = cognitoTokenRestClient.post()
                .uri("/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(CognitoTokenResponseDTO.class);
        
        if (response == null) {
            log.warn("Cognito M2M token response is null.");
            return null;
        }

        log.trace("Received response from Cognito M2M. JWTsExpiresIn={}", response.getExpiresIn());

        return response.getAccessToken();
    }
}