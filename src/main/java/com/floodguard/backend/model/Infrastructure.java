package com.floodguard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Geometry;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "infrastructure")
public class Infrastructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "infra_id")
    private Long infraId;

    @Column(length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "infra_type", nullable = false)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private InfraType type;

    @Column(length = 50)
    @Builder.Default
    private String status = "GOOD";

    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> properties;

    @Column(columnDefinition = "geometry(Geometry, 4326)")
    private Geometry geom;
}
