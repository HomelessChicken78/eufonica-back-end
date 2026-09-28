package it.eufonica.authservice.security;

import it.eufonica.authservice.exception.client.UnauthorizedException;
import it.eufonica.authservice.model.AccessMethod;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.repository.AccessMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class CurrentUserProvider {
    private final AccessMethodRepository accessMethodRepository;

    @Value("${PROVIDER_NAME:cognito}")
    private String providerName;

    /**
     * Retrieves the JWT from the current Spring Security context.
     *
     * @return the {@link Jwt} belonging to the currently authenticated user
     * @throws UnauthorizedException if the security context is empty or the user is not authenticated
     */
    public Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) throw new UnauthorizedException(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        return (Jwt) auth.getPrincipal();
    }

    /**
     * Extracts the subject (sub) claim from the current user's JWT.
     * This typically represents the unique identifier assigned to the user by the identity provider.
     *
     * @return the subject string from the authenticated user's JWT
     */
    public String getSub() {
        return getJwt().getSubject();
    }

    /**
     * Retrieves the access method associated with the currently authenticated user.
     * The lookup is performed using the configured identity provider name and the user's token subject.
     *
     * @return the {@link AccessMethod} corresponding to the current user and identity provider
     */
    public AccessMethod getAccessMethod() {
        return accessMethodRepository.findByProviderNameAndProviderUserId(providerName, getSub());
    }

    /**
     * Retrieves the application user associated with the current security context.
     *
     * @return the {@link AppUser} of the currently authenticated user
     */
    public AppUser getCurrentUser() {
        AccessMethod accessMethod = getAccessMethod();
        return accessMethod.getUser();
    }
}
