package com.floodguard.backend.exception;

import com.floodguard.backend.response.ApiResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception for business logic errors
 */
@Getter
public class CustomException extends RuntimeException {

    private final ApiResponse<Object> apiResponse;
    private final HttpStatus httpStatus;

    public CustomException(String message) {
        super(message);
        this.apiResponse = ApiResponse.error(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public CustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.apiResponse = ApiResponse.error(message);
        this.httpStatus = httpStatus;
    }
}
