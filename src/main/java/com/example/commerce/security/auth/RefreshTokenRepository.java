package com.example.commerce.security.auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository
        extends MongoRepository<RefreshToken, String> {

    void deleteAllByUserId(String userId);

    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
