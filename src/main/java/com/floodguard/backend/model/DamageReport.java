package com.floodguard.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "damage_reports")
public class DamageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // NULL nếu người dân không đăng nhập

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private DamageEvent event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private DamageArea area;

    @Column(name = "reporter_name")
    private String reporterName;

    @Column(name = "reporter_phone", length = 20)
    private String reporterPhone;

    @Column(name = "damage_type", length = 100)
    private String damageType; // house | crop | livestock | business | infrastructure

    @Min(1)
    @Max(5)
    @Column(name = "damage_level")
    private Integer damageLevel;

    @Column(name = "estimated_loss")
    private Long estimatedLoss; // VNĐ

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point geom;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DamageAsset> damageAssets = new ArrayList<>();
}
