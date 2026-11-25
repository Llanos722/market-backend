package com.tecnm.merida.market_backend.web.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey key;
    private final JwtParser parser;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new IllegalArgumentException("JWT_SECRET debe estar codificada en Base64");
        }
        if (decoded.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes al decodificar Base64");
        }
        key = Keys.hmacShaKeyFor(decoded);
        parser = Jwts.parser().verifyWith(key).build();
    }

    public String generateToken(String idCliente) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(idCliente)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String validateToken(String token) {
        var signedClaims = parser.parseSignedClaims(token);
        if (!"HS256".equals(signedClaims.getHeader().getAlgorithm())) {
            throw new JwtException("El token debe usar HS256");
        }
        var claims = signedClaims.getPayload();
        if (claims.getSubject() == null || claims.getSubject().isBlank() || claims.getExpiration() == null) {
            throw new JwtException("El token requiere subject y expiracion");
        }
        return claims.getSubject();
    }
}
