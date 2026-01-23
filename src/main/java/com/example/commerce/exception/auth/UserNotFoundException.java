package com.example.commerce.exception.auth;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AuthException {

    public UserNotFoundException() {
        super("User not found", HttpStatus.NOT_FOUND);
    }
}
