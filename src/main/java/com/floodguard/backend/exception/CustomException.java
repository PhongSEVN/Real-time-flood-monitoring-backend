package com.floodguard.backend.exception;

import lombok.Getter;
import com.floodguard.backend.constant.CommonConstant;
import com.floodguard.backend.response.ApiResponse;

import java.util.HashMap;

@Getter
public class CustomException extends Exception {
    private ApiResponse apiResponse;

    public CustomException(String message) {
        super(message);
        HashMap<String, String> result = new HashMap<>();
        result.put(CommonConstant.PARAM_MESSAGE, message);
        apiResponse = new ApiResponse(CommonConstant.NOT_OK, result);
    }
}
