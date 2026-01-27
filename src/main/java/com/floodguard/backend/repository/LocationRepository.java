package com.floodguard.backend.repository;

import com.floodguard.backend.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByLocationType(String locationType);

    List<Location> findByAddressContaining(String address);

    // Spatial query - find locations within a radius (in meters)
    @Query(value = "SELECT * FROM locations WHERE ST_DWithin(geom::geography, ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography, :radius)", nativeQuery = true)
    List<Location> findLocationsWithinRadius(
            @Param("lon") double longitude,
            @Param("lat") double latitude,
            @Param("radius") double radiusMeters);

    // Find locations within a bounding box
    @Query(value = "SELECT * FROM locations WHERE ST_Intersects(geom, ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326))", nativeQuery = true)
    List<Location> findLocationsInBoundingBox(
            @Param("minLon") double minLon,
            @Param("minLat") double minLat,
            @Param("maxLon") double maxLon,
            @Param("maxLat") double maxLat);

    // Find nearest location to a point
    @Query(value = "SELECT * FROM locations ORDER BY geom <-> ST_SetSRID(ST_Point(:lon, :lat), 4326) LIMIT :limit", nativeQuery = true)
    List<Location> findNearestLocations(
            @Param("lon") double longitude,
            @Param("lat") double latitude,
            @Param("limit") int limit);
}
