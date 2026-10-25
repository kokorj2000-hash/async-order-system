package com.aops.common.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

public final class JwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String TYPE_CLAIM = "type";
    private static final String ACCESS_TYPE = "access";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        if (!ACCESS_TYPE.equals(jwt.getClaimAsString(TYPE_CLAIM))) {
            throw new InvalidBearerTokenException("Access token required");
        }

        String role = jwt.getClaimAsString("role");

        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT does not contain role"
            );
        }

        return new JwtAuthenticationToken(
                jwt,
                List.of(
                        new SimpleGrantedAuthority("ROLE_" + role)
                ),
                jwt.getSubject()
        );
    }
}