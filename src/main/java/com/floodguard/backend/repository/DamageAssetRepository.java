package com.floodguard.backend.repository;

import com.floodguard.backend.model.DamageAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DamageAssetRepository extends JpaRepository<DamageAsset, UUID> {

    List<DamageAsset> findByReportId(UUID reportId);

    List<DamageAsset> findByAssetType(String assetType);

    @Query("SELECT SUM(a.estimatedValue) FROM DamageAsset a WHERE a.report.id = :reportId")
    Long getTotalValueByReport(@Param("reportId") UUID reportId);

    @Query("SELECT a.assetType, COUNT(a), SUM(a.estimatedValue) FROM DamageAsset a GROUP BY a.assetType")
    List<Object[]> getAssetStatistics();
}
