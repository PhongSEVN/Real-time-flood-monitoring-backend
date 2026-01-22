package com.floodguard.backend.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * ApiResponse class
 *
 * @author INIT
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ApiResponse {
    @JsonProperty("status")
    private Integer status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("result")
    private Object result;

    public ApiResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse(Integer status, Object result) {
        this.status = status;
        this.result = result;
    }
}
