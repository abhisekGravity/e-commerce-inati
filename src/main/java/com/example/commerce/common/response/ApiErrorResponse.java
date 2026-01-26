package com.example.commerce.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private String code;
    private Object message;
    private long timestamp;
}