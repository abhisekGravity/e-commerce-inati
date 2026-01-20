package com.example.commerce.security.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthTokens {
    private String accessToken;
    private String refreshToken;
}
