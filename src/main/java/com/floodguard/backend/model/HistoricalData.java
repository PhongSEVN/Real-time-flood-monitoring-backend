package com.floodguard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Polygon;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "historical_data")
public class HistoricalData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "alert_level", length = 50)
    private String alertLevel;

    @Column(name = "impact_summary", columnDefinition = "TEXT")
    private String impactSummary;

    @Column(name = "reference_geom", columnDefinition = "geometry(Polygon, 4326)")
    private Polygon referenceGeom;
}
