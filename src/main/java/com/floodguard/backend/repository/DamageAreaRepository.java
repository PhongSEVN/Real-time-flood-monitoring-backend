package com.floodguard.backend.repository;

import com.floodguard.backend.model.DamageArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DamageAreaRepository extends JpaRepository<DamageArea, UUID> {

    List<DamageArea> findByEventId(UUID eventId);

    List<DamageArea> findByRiskLevel(Integer riskLevel);

    List<DamageArea> findByRiskLevelGreaterThanEqual(Integer minRiskLevel);

    @Query("SELECT a FROM DamageArea a WHERE a.areaName LIKE %:name%")
    List<DamageArea> findByAreaNameContaining(@Param("name") String name);

    // Spatial query - find areas that contain a point
    @Query(value = "SELECT * FROM damage_areas WHERE ST_Contains(geom, ST_SetSRID(ST_Point(:lon, :lat), 4326))", nativeQuery = true)
    List<DamageArea> findAreasContainingPoint(@Param("lon") double longitude, @Param("lat") double latitude);

    // Find areas within a bounding box
    @Query(value = "SELECT * FROM damage_areas WHERE ST_Intersects(geom, ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326))", nativeQuery = true)
    List<DamageArea> findAreasInBoundingBox(
            @Param("minLon") double minLon,
            @Param("minLat") double minLat,
            @Param("maxLon") double maxLon,
            @Param("maxLat") double maxLat);
}
