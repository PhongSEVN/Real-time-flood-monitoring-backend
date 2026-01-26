package com.floodguard.backend.exception;

import com.floodguard.backend.response.ApiResponse;
import lombok.Getter;

/**
 * Custom exception for business logic errors
 */
@Getter
public class CustomException extends Exception {

    private final ApiResponse<Object> apiResponse;

    public CustomException(String message) {
        super(message);
        this.apiResponse = ApiResponse.error(message);
    }
}
