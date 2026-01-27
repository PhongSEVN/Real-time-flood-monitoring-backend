package com.floodguard.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class ReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        private Long locationId;

        private Long userId;

        // Hoặc tạo location mới
        private Double longitude;
        private Double latitude;
        private String address;

        @NotBlank(message = "Event type is required")
        private String eventType;

        private String description;

        private String imageUrl;

        @Min(value = 1, message = "Damage level must be between 1 and 5")
        @Max(value = 5, message = "Damage level must be between 1 and 5")
        private Integer damageLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String eventType;
        private String description;
        private String imageUrl;

        @Min(value = 1, message = "Damage level must be between 1 and 5")
        @Max(value = 5, message = "Damage level must be between 1 and 5")
        private Integer damageLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyRequest {
        @NotBlank(message = "Verify status is required")
        private String verifyStatus; // PENDING | VERIFIED | PROCESSED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long reportId;
        private Long locationId;
        private String locationAddress;
        private Double longitude;
        private Double latitude;
        private Long userId;
        private String userFullName;
        private LocalDateTime reportTime;
        private String eventType;
        private String description;
        private String imageUrl;
        private Integer damageLevel;
        private String verifyStatus;
    }
}
