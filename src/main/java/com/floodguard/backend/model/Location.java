package com.floodguard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;

    @Column(length = 255)
    private String address;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point geom;

    @Column(name = "location_type", length = 50)
    private String locationType;

    @Column(name = "base_elevation")
    @Builder.Default
    private Double baseElevation = 0.0;
}
