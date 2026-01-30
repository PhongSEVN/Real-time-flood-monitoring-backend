package com.floodguard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medical_resources")
public class MedicalResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long resourceId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 50)
    private String type;

    @Column(name = "contact_phone", length = 15)
    private String contactPhone;

    @Column(length = 100)
    private String specialization;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point geom;
}
