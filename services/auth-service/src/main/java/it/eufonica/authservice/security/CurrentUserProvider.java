package it.eufonica.authservice.security;

import it.eufonica.authservice.exception.client.UnauthorizedException;
import it.eufonica.authservice.exception.server.InternalServerErrorException;
import it.eufonica.authservice.model.AccessMethod;
import it.eufonica.authservice.model.AppUser;
import it.eufonica.authservice.repository.AccessMethodRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component @RequiredArgsConstructor @Slf4j
public class CurrentUserProvider {
    private final AccessMethodRepository accessMethodRepository;

    @Value("${PROVIDER_NAME:cognito}")
    private String providerName;

    /**
     * Gets the current request from the context.
     *
     * @return the current request.
     * @throws InternalServerErrorException if there is no request in the context
     */
    private HttpServletRequest getCurrentRequest() {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // A request should normally always be present.
        // If not, this method was called outside an HTTP request context
        // or from a thread without a bound request.
        if (attributes == null) throw new InternalServerErrorException("No request bound to the current thread.");

        return attributes.getRequest();
    }

    /**
     * Extracts the subject (sub) claim from the current request.
     * This typically represents the unique identifier assigned to the user by the identity provider.
     *
     * @return the subject string from the authenticated request
     * @throws UnauthorizedException if the gateway did not forward a subject
     */
    public String getSub() {
        String sub = getCurrentRequest().getHeader("X-User-Sub");

        if (sub == null || sub.isBlank())
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED.getReasonPhrase());

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
        // This shouldn't normally happen: a valid JWT with populated group claims
        // implies signup already completed, which always creates an AccessMethod.
        AccessMethod accessMethod = accessMethodRepository.findByProviderNameAndProviderUserId(providerName, getSub())
                .orElseThrow(() -> new InternalServerErrorException(
                        "Authenticated user with sub " + getSub() + " has no associated access method."));

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

    /**
     * Check if the current JWT's holder is an admin
     *
     * @return <ul>
     * <li>{@code true} if the token's holder is an admin</li>
     * <li>{@code false} if the token's holder is not an admin</li>
     * </ul>
     */
    public boolean isAdmin() {
        // TODO
        return true;
    }
}
