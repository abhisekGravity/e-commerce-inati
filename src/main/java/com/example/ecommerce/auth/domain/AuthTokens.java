package com.example.ecommerce.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
}
