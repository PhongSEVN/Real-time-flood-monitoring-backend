package com.floodguard.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DamageReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        private UUID userId; // Optional - null for anonymous report

        @NotNull(message = "Event ID is required")
        private UUID eventId;

        private UUID areaId;

        @NotBlank(message = "Reporter name is required")
        private String reporterName;

        private String reporterPhone;

        @NotBlank(message = "Damage type is required")
        private String damageType;

        @Min(value = 1, message = "Damage level must be between 1 and 5")
        @Max(value = 5, message = "Damage level must be between 1 and 5")
        private Integer damageLevel;

        private Long estimatedLoss;

        private String description;

        @NotNull(message = "Location is required")
        private Double longitude;

        @NotNull(message = "Location is required")
        private Double latitude;

        private List<DamageAssetDTO.CreateRequest> assets;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String reporterName;
        private String reporterPhone;
        private String damageType;

        @Min(value = 1, message = "Damage level must be between 1 and 5")
        @Max(value = 5, message = "Damage level must be between 1 and 5")
        private Integer damageLevel;

        private Long estimatedLoss;
        private String description;
        private Double longitude;
        private Double latitude;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private UUID userId;
        private UUID eventId;
        private String eventType;
        private UUID areaId;
        private String areaName;
        private String reporterName;
        private String reporterPhone;
        private String damageType;
        private Integer damageLevel;
        private Long estimatedLoss;
        private String description;
        private Double longitude;
        private Double latitude;
        private Boolean isVerified;
        private LocalDateTime createdAt;
        private List<DamageAssetDTO.Response> assets;
        private Long totalAssetValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyRequest {
        @NotNull
        private Boolean isVerified;
    }
}
