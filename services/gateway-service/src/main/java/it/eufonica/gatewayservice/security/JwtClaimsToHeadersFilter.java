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
    private static final String GROUPS_CLAIM = "cognito:groups";

    @Value("${ARTIST_ID_CLAIM:artist_id}")
    private String artistIdClaim;

    @Override @NullMarked
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var headerAuth = exchange.getRequest().getHeaders().getFirst("Authorization");

        // Remove the Headers to prevent spoofing
        ServerHttpRequest strippedHeaders = exchange.getRequest()
                .mutate()
                .headers(h -> {
                    h.remove("X-User-Sub");
                    h.remove("X-User-Roles");
                    h.remove("X-Artist-Id");
                })
                .build();
        var strippedExchange = exchange.mutate().request(strippedHeaders).build();
        log.trace("Headers X-User-Sub, X-User-Roles, and X-Artist-Id might have been removed from the original request.");

        // Search for the jwt
        if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
            log.trace("No Bearer token present, forwarding request without identity headers.");
            return chain.filter(strippedExchange);
        }

        // Try to put the jwt, the roles and the artist id as headers
        return jwtDecoder.decode(headerAuth.substring("Bearer ".length()))
                .flatMap(jwt -> {
                    String sub = jwt.getSubject();
                    List<String> groups = jwt.getClaimAsStringList(GROUPS_CLAIM);
                    String artistId = jwt.getClaimAsString(artistIdClaim);

                    log.debug("Decoded JWT claims. sub={}, groups={}, artistIdClaim={}, artistId={}",
                            sub, groups, artistIdClaim, artistId);

                    ServerHttpRequest newReq = strippedExchange.getRequest()
                            .mutate()
                            .headers(h -> {
                                h.set("X-User-Sub", sub);

                                // Avoid putting the headers if the given data is null
                                if (groups != null && !groups.isEmpty())
                                    h.set("X-User-Roles", String.join(",", groups));
                                else
                                    log.trace("No groups claims found in the request. Forwarding request without X-User-Roles header.");
                                if (artistId != null)
                                    h.set("X-Artist-Id", artistId);
                                else
                                    log.trace("No {} claims found in the request. Forwarding request without X-Artist-Id header.", artistIdClaim);
                            })
                            .build();

                    return chain.filter(strippedExchange.mutate().request(newReq).build());
                })

                // If it fails, continue with the filters without doing anything
                .onErrorResume(
                        throwable -> {
                            log.warn("JWT decoding failed, forwarding request without identity headers. reason={}",
                                    throwable.getMessage());
                            return chain.filter(strippedExchange);
                        }
                );
    }
}
