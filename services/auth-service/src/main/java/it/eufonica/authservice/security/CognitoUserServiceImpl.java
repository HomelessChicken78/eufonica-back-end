package it.eufonica.authservice.security;

import it.eufonica.exception.CommonErrorReason;
import it.eufonica.exception.server.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import java.util.UUID;

@Service
@RequiredArgsConstructor @Slf4j
public class CognitoUserServiceImpl implements CognitoUserService {
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${COGNITO_USER_POOL_ID}")
    private String cognitoUserPoolId;

    @Value("${COGNITO_ATTRIBUTE_ARTIST_ID_NAME:custom:artist_id}")
    private String attributeName;

    @Value("${COGNITO_USER_GROUP_NAME:user}")
    private String userGroupName;

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
        } catch (CognitoIdentityProviderException | SdkClientException e) {
            throw new InternalServerErrorException(
                    "Cognito adminUpdateUserAttributes failed while setting artist affiliation. sub=" + sub + ", artistId=" + artistId,
                    CommonErrorReason.INTERNAL_ERROR);
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
        } catch (CognitoIdentityProviderException | SdkClientException e) {
            throw new InternalServerErrorException(
                    "Cognito adminUpdateUserAttributes failed while clearing artist affiliation. sub=" + sub,
                    CommonErrorReason.INTERNAL_ERROR);
        }
    }

    @Override
    public void addToUserGroup(String sub) {
        log.info("Adding user with sub={} to the user group", sub);
        try {
            cognitoClient.adminAddUserToGroup(builder -> builder
                    .userPoolId(cognitoUserPoolId)
                    .username(sub)
                    .groupName(userGroupName)
            );
        } catch (CognitoIdentityProviderException | SdkClientException e) {
            throw new InternalServerErrorException(
                    "Cognito adminAddUserToGroup failed. sub=" + sub + ", group=" + userGroupName,
                    CommonErrorReason.INTERNAL_ERROR);
        }
    }
}
