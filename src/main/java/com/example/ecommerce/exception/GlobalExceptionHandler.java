package com.example.ecommerce.exception;

import com.example.ecommerce.common.response.ApiErrorResponse;
import com.example.ecommerce.exception.auth.AuthException;
import com.example.ecommerce.exception.cart.CartException;
import com.example.ecommerce.exception.order.OrderException;
import com.example.ecommerce.exception.product.InvalidProductRequestException;
import com.example.ecommerce.exception.product.ProductException;
import com.example.ecommerce.exception.tenant.TenantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Arrays;
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
                        Instant.now().toEpochMilli()
                ));
    }

    @ExceptionHandler(TenantException.class)
    public ResponseEntity<ApiErrorResponse> handleTenant(TenantException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(
                        "TENANT_ERROR",
                        ex.getMessage(),
                        Instant.now().toEpochMilli()
                ));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiErrorResponse> handleAuth(AuthException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(
                        "AUTH_ERROR",
                        ex.getMessage(),
                        Instant.now().toEpochMilli()
                ));
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<ApiErrorResponse> handleProduct(ProductException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(
                        "PRODUCT_ERROR",
                        ex.getMessage(),
                        Instant.now().toEpochMilli()
                ));
    }

    @ExceptionHandler(CartException.class)
    public ResponseEntity<ApiErrorResponse> handleDiscountRule(CartException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(
                        "CART_ERROR",
                        ex.getMessage(),
                        Instant.now().toEpochMilli()
                ));
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ApiErrorResponse> handleOrder(OrderException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ApiErrorResponse(
                        "ORDER_ERROR",
                        ex.getMessage(),
                        Instant.now().toEpochMilli()
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleEnumMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {

            Object[] constants = ex.getRequiredType().getEnumConstants();
            String allowedValues = Arrays.toString(constants);
            String value = ex.getValue() != null ? ex.getValue().toString() : "null";

            InvalidProductRequestException productEx = new InvalidProductRequestException(
                    "Invalid value '" + value + "' for parameter '" + ex.getName() +
                            "'. Allowed values: " + allowedValues
            );

            return handleProduct(productEx);
        }

        ApiErrorResponse response = new ApiErrorResponse(
                "INVALID_REQUEST",
                ex.getMessage(),
                Instant.now().toEpochMilli()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnhandled(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        ex.getMessage(),
                        Instant.now().toEpochMilli()
                ));
    }
}