package com.floodguard.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class HistoricalDataDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {

        @NotNull(message = "Year is required")
        private Integer year;

        private String alertLevel;

        private String impactSummary;

        // WKT format: POLYGON((lon lat, lon lat, ...))
        private String referenceGeomWkt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private Integer year;
        private String alertLevel;
        private String impactSummary;
        private String referenceGeomWkt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long historyId;
        private Integer year;
        private String alertLevel;
        private String impactSummary;
        private String referenceGeomWkt;
    }
}
