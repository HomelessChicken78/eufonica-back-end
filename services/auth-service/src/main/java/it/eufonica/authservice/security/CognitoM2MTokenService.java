package it.eufonica.authservice.security;

public interface CognitoM2MTokenService {
    /**
     * Obtains a client-credentials access token from Cognito for service-to-service calls.
     *
     * @return a valid Bearer access token
     */
    String getAccessToken();
}
