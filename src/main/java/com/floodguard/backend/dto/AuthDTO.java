package com.floodguard.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class AuthDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoogleLoginRequest {
        @NotBlank(message = "ID Token is required")
        private String idToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Phone number is required")
        private String phoneNumber;

        @NotBlank(message = "Password is required")
        private String password;

        private String role;
        private String addressGroup;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String tokenType;
        private Long expiresIn;
        private UserInfo user;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String role;
        private Integer priorityLevel;
        private String addressGroup;
        private boolean isNewUser;
    }
}
