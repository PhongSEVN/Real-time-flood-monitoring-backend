package com.floodguard.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class DamageAreaDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        @NotNull(message = "Event ID is required")
        private UUID eventId;

        @NotBlank(message = "Area name is required")
        private String areaName;

        // GeoJSON format polygon coordinates
        @NotNull(message = "Geometry is required")
        private String geomWkt; // WKT format: POLYGON((lon lat, lon lat, ...))

        @Min(value = 1, message = "Risk level must be between 1 and 5")
        @Max(value = 5, message = "Risk level must be between 1 and 5")
        private Integer riskLevel;

        private Integer estimatedHouseholds;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String areaName;
        private String geomWkt;

        @Min(value = 1, message = "Risk level must be between 1 and 5")
        @Max(value = 5, message = "Risk level must be between 1 and 5")
        private Integer riskLevel;

        private Integer estimatedHouseholds;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private UUID eventId;
        private String eventType;
        private String areaName;
        private String geomWkt;
        private Integer riskLevel;
        private Integer estimatedHouseholds;
        private LocalDateTime createdAt;
        private Integer reportsCount;
    }
}
