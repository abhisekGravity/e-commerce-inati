package com.example.ecommerce.User.repository;

import com.example.ecommerce.User.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByTenantIdAndEmail(String tenantId, String email);

    boolean existsByTenantIdAndEmail(String tenantId, String email);

    Optional<User> findByIdAndTenantId(String userId, String tenantId);
}
