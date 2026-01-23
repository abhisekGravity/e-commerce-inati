package com.example.commerce.exception.auth;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends AuthException {

    public UserAlreadyExistsException() {
        super("Email already in use for this tenant", HttpStatus.CONFLICT);
    }
}