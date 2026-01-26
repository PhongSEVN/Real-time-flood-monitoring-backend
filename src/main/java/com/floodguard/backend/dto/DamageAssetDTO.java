package com.floodguard.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

public class DamageAssetDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        @NotBlank(message = "Asset type is required")
        private String assetType;

        @NotBlank(message = "Asset name is required")
        private String assetName;

        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        private String unit;

        @Min(value = 0, message = "Estimated value cannot be negative")
        private Long estimatedValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String assetType;
        private String assetName;
        private Integer quantity;
        private String unit;
        private Long estimatedValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private UUID reportId;
        private String assetType;
        private String assetName;
        private Integer quantity;
        private String unit;
        private Long estimatedValue;
    }
}
