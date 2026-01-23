package com.example.commerce.exception;

import com.example.commerce.exception.auth.AuthException;
import com.example.commerce.exception.tenant.TenantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .toList();

        return ResponseEntity
                .badRequest()
                .body(new ApiErrorResponse(
                        "VALIDATION_ERROR",
                        errors,
                        System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiErrorResponse> handleAuth(AuthException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(
                        "AUTH_ERROR",
                        ex.getMessage(),
                        System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(TenantException.class)
    public ResponseEntity<ApiErrorResponse> handleTenant(TenantException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(
                        "TENANT_ERROR",
                        ex.getMessage(),
                        System.currentTimeMillis()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandled(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        "Something went wrong",
                        System.currentTimeMillis()
                ));
    }
}