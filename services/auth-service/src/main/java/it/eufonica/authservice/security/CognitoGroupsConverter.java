package it.eufonica.authservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;

public class CognitoGroupsConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String GROUPS_CLAIM = "cognito:groups";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> groups = jwt.getClaimAsStringList(GROUPS_CLAIM);

        Collection<GrantedAuthority> authorities = (groups == null ? List.<String>of() : groups).stream()
                .map(g -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + g.toUpperCase()))
                .toList();

        return new JwtAuthenticationToken(jwt, authorities);
    }
}