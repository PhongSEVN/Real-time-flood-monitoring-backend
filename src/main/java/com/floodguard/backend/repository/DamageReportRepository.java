package com.floodguard.backend.repository;

import com.floodguard.backend.model.DamageReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DamageReportRepository extends JpaRepository<DamageReport, UUID> {

    List<DamageReport> findByEventId(UUID eventId);

    List<DamageReport> findByAreaId(UUID areaId);

    List<DamageReport> findByUserId(UUID userId);

    List<DamageReport> findByDamageType(String damageType);

    List<DamageReport> findByIsVerified(Boolean isVerified);

    List<DamageReport> findByDamageLevelGreaterThanEqual(Integer minLevel);

    @Query("SELECT r FROM DamageReport r ORDER BY r.createdAt DESC")
    List<DamageReport> findAllOrderByCreatedAtDesc();

    // Spatial query - find reports within a radius (in meters)
    @Query(value = "SELECT * FROM damage_reports WHERE ST_DWithin(geom::geography, ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography, :radius)", nativeQuery = true)
    List<DamageReport> findReportsWithinRadius(
            @Param("lon") double longitude,
            @Param("lat") double latitude,
            @Param("radius") double radiusMeters);

    // Find reports within a specific area
    @Query(value = "SELECT dr.* FROM damage_reports dr JOIN damage_areas da ON ST_Within(dr.geom, da.geom) WHERE da.id = :areaId", nativeQuery = true)
    List<DamageReport> findReportsWithinArea(@Param("areaId") UUID areaId);

    // Statistics: count by damage type
    @Query("SELECT r.damageType, COUNT(r) FROM DamageReport r GROUP BY r.damageType")
    List<Object[]> countByDamageType();

    // Statistics: total estimated loss
    @Query("SELECT SUM(r.estimatedLoss) FROM DamageReport r WHERE r.event.id = :eventId")
    Long getTotalEstimatedLossByEvent(@Param("eventId") UUID eventId);
}
