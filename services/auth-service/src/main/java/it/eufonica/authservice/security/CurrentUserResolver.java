package it.eufonica.authservice.security;

import it.eufonica.authservice.exception.AuthErrorReason;
import it.eufonica.authservice.model.AccessMethod;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.repository.AccessMethodRepository;
import it.eufonica.exception.client.UnauthorizedException;
import it.eufonica.security.CurrentCallerProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class CurrentUserResolver {
    // Repository
    private final AccessMethodRepository accessMethodRepository;

    // Mapper & Utility
    private final CurrentCallerProvider currentCaller;

    @Value("${PROVIDER_NAME:cognito}")
    private String providerName;

    /**
     * Retrieves the access method associated with the currently authenticated user.
     * The lookup is performed using the configured identity provider name and the user's token subject.
     *
     * @return the {@link AccessMethod} corresponding to the current user and identity provider
     * @throws UnauthorizedException if the access method in the jwt doesn't exist
     */
    public AccessMethod getAccessMethod() {
        // If the JWT has a valid sub, but the sub can't be brought back to a user, either of the following happened:
        // 1. The user logged in Cognito-side, but not application side - most common scenario
        // 2. The caller did log in previously (both Cognito and application side), but their user was removed
        AccessMethod accessMethod = accessMethodRepository.findByProviderNameAndProviderUserId(providerName, currentCaller.getSub())
                .orElseThrow(() -> new UnauthorizedException(
                        "Authenticated user with sub " + currentCaller.getSub() + " has no associated access method.",
                        AuthErrorReason.IDENTITY_NOT_RECOGNIZED));

        log.debug("Retrieved access method from the current security context. accessMethod={}", accessMethod);

        return accessMethod;
    }

    /**
     * Retrieves the application user associated with the current security context.
     *
     * @return the {@link AppUser} of the currently authenticated user
     */
    public AppUser getCurrentUser() {
        AccessMethod accessMethod = getAccessMethod();
        AppUser user = accessMethod.getUser();

        log.debug("Retrieved user from the current security context. user={}", user);

        return user;
    }
}
