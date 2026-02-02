package com.example.ecommerce.auth.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("refresh_tokens")
@Data
@Builder
public class RefreshToken {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String tenantId;

    private String tokenHash;

    private Instant expiresAt;

}
