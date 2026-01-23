package com.example.commerce.exception.auth;

import org.springframework.http.HttpStatus;

public class RefreshTokenExpiredException extends AuthException {

    public RefreshTokenExpiredException() {
        super("Refresh token expired", HttpStatus.UNAUTHORIZED);
    }
}
