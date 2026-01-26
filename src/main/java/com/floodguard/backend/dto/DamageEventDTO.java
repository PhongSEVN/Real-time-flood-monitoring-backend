package com.floodguard.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class DamageEventDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        @NotBlank(message = "Event type is required")
        private String eventType;

        private String description;

        private LocalDateTime startTime;

        private LocalDateTime endTime;

        @Min(value = 1, message = "Severity level must be between 1 and 5")
        @Max(value = 5, message = "Severity level must be between 1 and 5")
        private Integer severityLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {

        private String eventType;
        private String description;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        @Min(value = 1, message = "Severity level must be between 1 and 5")
        @Max(value = 5, message = "Severity level must be between 1 and 5")
        private Integer severityLevel;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private String eventType;
        private String description;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Integer severityLevel;
        private LocalDateTime createdAt;
        private Integer areasCount;
        private Integer reportsCount;
    }
}
