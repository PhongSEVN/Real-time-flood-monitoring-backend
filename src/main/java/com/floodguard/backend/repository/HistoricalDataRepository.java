package com.floodguard.backend.repository;

import com.floodguard.backend.model.HistoricalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalDataRepository extends JpaRepository<HistoricalData, Long> {

    List<HistoricalData> findByYear(Integer year);

    List<HistoricalData> findByAlertLevel(String alertLevel);

    List<HistoricalData> findByYearBetween(Integer startYear, Integer endYear);

    @Query("SELECT h FROM HistoricalData h ORDER BY h.year DESC")
    List<HistoricalData> findAllOrderByYearDesc();

    // Spatial query - find historical data that intersects with a point
    @Query(value = "SELECT * FROM historical_data WHERE ST_Intersects(reference_geom, ST_SetSRID(ST_Point(:lon, :lat), 4326))", nativeQuery = true)
    List<HistoricalData> findHistoryContainingPoint(@Param("lon") double longitude, @Param("lat") double latitude);

    // Find historical data within a bounding box
    @Query(value = "SELECT * FROM historical_data WHERE ST_Intersects(reference_geom, ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326))", nativeQuery = true)
    List<HistoricalData> findHistoryInBoundingBox(
            @Param("minLon") double minLon,
            @Param("minLat") double minLat,
            @Param("maxLon") double maxLon,
            @Param("maxLat") double maxLat);
}
