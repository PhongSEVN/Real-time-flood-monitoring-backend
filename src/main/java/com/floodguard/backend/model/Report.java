package com.floodguard.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "guest_name", length = 100)
    private String guestName;

    @Column(name = "guest_phone", length = 15)
    private String guestPhone;

    @Column(name = "event_type", length = 50)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "damage_level")
    @Builder.Default
    private Integer damageLevel = 0;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "report_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Builder.Default
    private ReportStatus status = ReportStatus.UNVERIFIED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point geom;
}
