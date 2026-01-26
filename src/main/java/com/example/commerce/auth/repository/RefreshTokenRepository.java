package com.example.commerce.auth.repository;

import com.example.commerce.auth.domain.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends MongoRepository<RefreshToken, String> {

    void deleteAllByUserId(String userId);

    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
