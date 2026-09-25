package it.eufonica.authservice.security;

import it.eufonica.authservice.exception.server.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

import java.util.UUID;

@Service
@RequiredArgsConstructor @Slf4j
public class CognitoUserServiceImpl implements CognitoUserService {
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${COGNITO_USER_POOL_ID}")
    private String cognitoUserPoolId;

    @Value("${COGNITO_ATTRIBUTE_ARTIST_ID_NAME:custom:artist_id}")
    private String attributeName;

    @Override
    public void setAffiliatedArtist(String sub, UUID artistId) {
        log.info("setAffiliatedArtist({}, {})", sub, artistId);
        AttributeType artistIdAttribute = AttributeType.builder()
                .name(attributeName)
                .value(artistId.toString())
                .build();

        try {
            cognitoClient.adminUpdateUserAttributes(builder -> builder
                    .username(sub)
                    .userPoolId(cognitoUserPoolId)
                    .userAttributes(artistIdAttribute)
            );
        } catch (RuntimeException e) {
            log.error("Unexpected error while trying to affiliate an user to an artist.", e);
            throw new InternalServerErrorException(
                    "Unknown error while setting the user as an artist. It might be a connection problem. Please try again later.");
        }
    }

    @Override
    public void clearAffiliatedArtist(String sub) {
        log.info("clearAffiliatedArtist({})", sub);
        AttributeType artistIdAttribute = AttributeType.builder()
                .name(attributeName)
                .build();
        try {
            cognitoClient.adminUpdateUserAttributes(builder -> builder
                    .username(sub)
                    .userPoolId(cognitoUserPoolId)
                    .userAttributes(artistIdAttribute)
            );
        } catch (RuntimeException e) {
            log.error("Unexpected error while trying to remove the artist affiliation for a user.", e);
            throw new InternalServerErrorException(
                    "Unknown error while removing the user's artist affiliation. It might be a connection problem. Please try again later.");
        }
    }
}
