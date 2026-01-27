package com.floodguard.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class UserDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        @NotBlank(message = "Full name is required")
        private String fullName;

        @Email(message = "Invalid email format")
        private String email;

        private String phoneNumber;

        private String role; // RESIDENT | GROUP_LEADER | WARD_OFFICIAL

        private Integer priorityLevel;

        private String addressGroup;

        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String fullName;

        @Email(message = "Invalid email format")
        private String email;

        private String phoneNumber;
        private String role;
        private Integer priorityLevel;
        private String addressGroup;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long userId;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String role;
        private Integer priorityLevel;
        private String addressGroup;
        private Integer reportsCount;
    }
}
