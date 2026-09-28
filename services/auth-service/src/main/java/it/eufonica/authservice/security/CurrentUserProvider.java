package it.eufonica.authservice.security;

import it.eufonica.authservice.exception.client.UnauthorizedException;
import it.eufonica.authservice.model.AccessMethod;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.repository.AccessMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
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
        var jwt = (Jwt) auth.getPrincipal();

        log.trace("Retrieved JWT from the current security context.");

        return jwt;
    }

    /**
     * Extracts the subject (sub) claim from the current user's JWT.
     * This typically represents the unique identifier assigned to the user by the identity provider.
     *
     * @return the subject string from the authenticated user's JWT
     */
    public String getSub() {
        String sub = getJwt().getSubject();

        log.trace("Retrieved sub from the current security context. sub={}", sub);

        return sub;
    }

    /**
     * Retrieves the access method associated with the currently authenticated user.
     * The lookup is performed using the configured identity provider name and the user's token subject.
     *
     * @return the {@link AccessMethod} corresponding to the current user and identity provider
     */
    public AccessMethod getAccessMethod() {
        var accessMethod = accessMethodRepository.findByProviderNameAndProviderUserId(providerName, getSub());

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
