package com.example.ecommerce.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final RsaKeyProvider keyProvider;

    public JwtService(RsaKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    public String generateAccessToken(String userId, String tenantId, int tokenVersion) {
        try {

            return Jwts.builder()
                    .setSubject(userId)
                    .claim("tenantId", tenantId)
                    .claim("tokenVersion", tokenVersion)
                    .setIssuedAt(new Date())
                    .setExpiration(Date.from(Instant.now().plusSeconds(900)))
                    .signWith(keyProvider.getPrivateKey(), SignatureAlgorithm.RS256)
                    .compact();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }

    public Claims validateToken(String token) {
        try {
            PublicKey publicKey = keyProvider.getPublicKey();

            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT", e);
        }
    }
}
