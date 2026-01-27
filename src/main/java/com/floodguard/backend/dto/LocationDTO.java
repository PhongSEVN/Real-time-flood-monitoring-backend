package com.floodguard.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class LocationDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        @NotBlank(message = "Address is required")
        private String address;

        @NotNull(message = "Longitude is required")
        private Double longitude;

        @NotNull(message = "Latitude is required")
        private Double latitude;

        private String locationType;

        private Double baseElevation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String address;
        private Double longitude;
        private Double latitude;
        private String locationType;
        private Double baseElevation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long locationId;
        private String address;
        private Double longitude;
        private Double latitude;
        private String locationType;
        private Double baseElevation;
        private Integer reportsCount;
    }
}
