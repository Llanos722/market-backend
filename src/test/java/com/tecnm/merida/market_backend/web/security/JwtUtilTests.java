package com.tecnm.merida.market_backend.web.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTests {
    @Test
    void rejectsMissingInvalidAndShortSecrets() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil(null));
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil("not base64!"));
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil(""));
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil(Base64.getEncoder().encodeToString(new byte[31])));
    }

    @Test
    void enforcesHs256EvenWithALongerKey() {
        var key = Jwts.SIG.HS512.key().build();
        var jwtUtil = new JwtUtil(Base64.getEncoder().encodeToString(key.getEncoded()));
        String token = jwtUtil.generateToken("100111");
        assertEquals("100111", jwtUtil.validateToken(token));
        assertEquals("HS256", Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getHeader().getAlgorithm());
        String hs512 = Jwts.builder().subject("100111").expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(key, Jwts.SIG.HS512).compact();
        assertThrows(JwtException.class, () -> jwtUtil.validateToken(hs512));
    }
}
