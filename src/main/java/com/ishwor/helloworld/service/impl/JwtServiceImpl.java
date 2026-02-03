package com.ishwor.helloworld.service.impl;

import com.ishwor.helloworld.config.JwtProperties;
import com.ishwor.helloworld.entity.Role;
import com.ishwor.helloworld.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;


@Service
public class JwtServiceImpl implements JwtService {
    private final JwtProperties jwtProperties;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateToken(Long userId, String email, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(15 * 60); //15 min

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(Map.of(
                        "email",email,
                        "role",role
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractSubject(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public String extractRole(String token) {
        Object role = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role");
        return role == null ? null : role.toString();
    }
}
