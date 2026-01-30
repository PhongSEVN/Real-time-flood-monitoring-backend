package com.floodguard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "climate_data")
public class ClimateData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id")
    private Long dataId;

    @Column(name = "record_date", nullable = false)
    private LocalDateTime recordDate;

    @Column(length = 20)
    private String type; // RAIN, TIDE

    @Column(nullable = false)
    private Double value;

    @Column(name = "is_forecast")
    @Builder.Default
    private Boolean isForecast = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "geometry(Polygon, 4326)")
    private Polygon geom;
}
