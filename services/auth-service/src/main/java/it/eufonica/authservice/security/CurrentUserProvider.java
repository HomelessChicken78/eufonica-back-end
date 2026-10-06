package it.eufonica.authservice.security;

import it.eufonica.authservice.exception.client.ForbiddenException;
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

import java.util.Arrays;
import java.util.List;

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
     * Gets the roles present in the headers.
     *
     * @return A list of uppercase strings containing all the roles
     */
    public List<String> getRoles() {
        String rolesHeader = getCurrentRequest().getHeader("X-User-Roles");
        if (rolesHeader == null || rolesHeader.isBlank()) return List.of();

        List<String> roles = Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .toList();

        log.debug("User has the following roles={} ({} roles present)", roles, roles.size());
        return roles;
    }

    /**
     * Check if the current caller is an admin.
     *
     * @return <ul>
     * <li>{@code true} if the token's holder is an admin</li>
     * <li>{@code false} if the token's holder is not an admin</li>
     * </ul>
     */
    public boolean isAdmin() {
        return getRoles().contains("ADMIN");
    }

    /**
     * Check if the current caller has the "user" role.
     *
     * @throws ForbiddenException if the caller does not have the "user" role
     */
    public void requireUser() {
        if (!getRoles().contains("USER"))
            throw new ForbiddenException("This action require an USER role.");
    }

    /**
     * Check if the current caller is an admin.
     *
     * @throws ForbiddenException if the caller is not an admin
     */
    public void requireAdmin() {
        if (!isAdmin())
            throw new ForbiddenException("This action require an ADMIN role.");
    }

    /**
     * Gets the artist id present in the headers.
     *
     * @return A string containing the artist id or {@code null} if there is no {@code X-Artist-Id} header
     */
    public String getArtistId() {
        return getCurrentRequest().getHeader("X-Artist-Id");
    }
}
