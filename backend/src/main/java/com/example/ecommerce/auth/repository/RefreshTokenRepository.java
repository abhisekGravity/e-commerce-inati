package com.example.ecommerce.auth.repository;

import com.example.ecommerce.auth.domain.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends MongoRepository<RefreshToken, String> {

    void deleteAllByUserId(String userId);

    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
