package com.floodguard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "households")
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "household_id")
    private Long householdId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String address;

    @Column(name = "num_people")
    @Builder.Default
    private Integer numPeople = 1;

    @Column(name = "has_elderly")
    @Builder.Default
    private Boolean hasElderly = false;

    @Column(name = "has_children")
    @Builder.Default
    private Boolean hasChildren = false;

    @Column(name = "has_sick_person")
    @Builder.Default
    private Boolean hasSickPerson = false;

    @Column(name = "has_pregnant")
    @Builder.Default
    private Boolean hasPregnant = false;

    @Column(name = "num_floors")
    private Integer numFloors;

    @Column(name = "business_type", length = 100)
    private String businessType;

    @Column(name = "asset_description", columnDefinition = "TEXT")
    private String assetDescription;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point geom;
}
