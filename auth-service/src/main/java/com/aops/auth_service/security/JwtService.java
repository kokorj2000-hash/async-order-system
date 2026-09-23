package com.aops.auth_service.security;

import com.aops.auth_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long accessTokenExpiration;

    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(User user) {

        return generateToken(
                user,
                accessTokenExpiration
        );
    }

    public String generateRefreshToken(User user) {

        return generateToken(
                user,
                refreshTokenExpiration
        );
    }

    private String generateToken(
            User user,
            long expiration) {

        Date now = new Date();

        Date expirationDate =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey, Jwts.SIG.HS384)
                .compact();
    }

    public String extractUserId(String token) {

        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {

        return getClaims(token)
                .get("role", String.class);
    }

    public boolean isTokenValid(String token) {

        try {
            getClaims(token);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}