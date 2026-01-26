package com.floodguard.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "damage_assets")
public class DamageAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private DamageReport report;

    @Column(name = "asset_type", length = 100)
    private String assetType;

    @Column(name = "asset_name")
    private String assetName;

    private Integer quantity;

    @Column(length = 50)
    private String unit;

    @Column(name = "estimated_value")
    private Long estimatedValue;
}
