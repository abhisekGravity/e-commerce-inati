package com.example.commerce.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getBindingResult().getAllErrors()
                        .stream()
                        .map(err -> err.getDefaultMessage())
                        .toList());
    }
}
