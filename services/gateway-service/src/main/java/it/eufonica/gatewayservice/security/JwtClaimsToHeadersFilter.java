package it.eufonica.gatewayservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor @Slf4j
public class JwtClaimsToHeadersFilter implements WebFilter {
    private final ReactiveJwtDecoder jwtDecoder;

    // Claims
    private static final String GROUPS_CLAIM = "cognito:groups";
    @Value("${ARTIST_ID_CLAIM:artist_id}") private String artistIdClaim;

    // Header names
    private static final String USER_SUB = "X-User-Sub";
    private static final String USER_ROLES = "X-User-Roles";
    private static final String ARTIST_ID = "X-Artist-Id";

    @Override @NullMarked
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var headerAuth = exchange.getRequest().getHeaders().getFirst("Authorization");

        // Remove the Headers to prevent spoofing
        ServerHttpRequest strippedHeaders = exchange.getRequest()
                .mutate()
                .headers(h -> {
                    h.remove(USER_SUB);
                    h.remove(USER_ROLES);
                    h.remove(ARTIST_ID);
                })
                .build();
        var strippedExchange = exchange.mutate().request(strippedHeaders).build();
        log.trace("Headers {}, {}, and {} might have been removed from the original request.",
                USER_SUB, USER_ROLES, ARTIST_ID);

        // Search for the jwt
        if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
            log.trace("No Bearer token present, forwarding request without identity headers.");
            return chain.filter(strippedExchange);
        }

        // Try to put the jwt, the roles and the artist id as headers
        return jwtDecoder.decode(headerAuth.substring("Bearer ".length()))
                // If it fails the decoding, just log a warn and return an empty Mono
                .onErrorResume(
                        throwable -> {
                            log.warn("JWT decoding failed, forwarding request without identity headers. reason={}",
                                    throwable.getMessage());

                            // We return an empty mono. This way the below ".switchIfEmpty" can detect it and correctly continue with the filters
                            return Mono.empty();
                        }
                )

                .flatMap(jwt -> {
                    String sub = jwt.getSubject();
                    List<String> groups = jwt.getClaimAsStringList(GROUPS_CLAIM);
                    String artistId = jwt.getClaimAsString(artistIdClaim);

                    log.debug("Decoded JWT claims. sub={}, groups={}, artistIdClaim={}, artistId={}",
                            sub, groups, artistIdClaim, artistId);

                    ServerHttpRequest newReq = strippedExchange.getRequest()
                            .mutate()
                            .headers(h -> {
                                h.set(USER_SUB, sub);

                                // Avoid putting the headers if the given data is null
                                if (groups != null && !groups.isEmpty())
                                    h.set(USER_ROLES, String.join(",", groups));
                                else
                                    log.trace("No groups claims found in the request. Forwarding request without {} header.", USER_ROLES);
                                if (artistId != null)
                                    h.set(ARTIST_ID, artistId);
                                else
                                    log.trace("No {} claims found in the request. Forwarding request without {} header.", artistIdClaim, ARTIST_ID);
                            })
                            .build();

                    return chain.filter(strippedExchange.mutate().request(newReq).build());
                })

                // If the Mono is empty, it means the decoding failed. Continue with the filters without doing anything
                .switchIfEmpty(chain.filter(strippedExchange));
    }
}
