package com.floodguard.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Polygon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "damage_areas")
public class DamageArea {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private DamageEvent event;

    @Column(name = "area_name")
    private String areaName;

    @Column(columnDefinition = "geometry(Polygon, 4326)")
    private Polygon geom;

    @Min(1)
    @Max(5)
    @Column(name = "risk_level")
    private Integer riskLevel;

    @Column(name = "estimated_households")
    private Integer estimatedHouseholds;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DamageReport> damageReports = new ArrayList<>();
}
