package com.floodguard.backend.repository;

import com.floodguard.backend.model.Report;
import com.floodguard.backend.model.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

        // Find by user's ID (using property path)
        List<Report> findByUserUserId(Long userId);

        List<Report> findByEventType(String eventType);

        List<Report> findByStatus(ReportStatus status);

        List<Report> findByDamageLevelGreaterThanEqual(Integer damageLevel);

        // Find by time range
        List<Report> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

        @Query("SELECT r FROM Report r ORDER BY r.createdAt DESC")
        List<Report> findAllOrderByCreatedAtDesc();

        // Find pending reports for verification
        @Query("SELECT r FROM Report r WHERE r.status = 'UNVERIFIED' ORDER BY r.createdAt DESC")
        List<Report> findUnverifiedReports();

        // Statistics - count by event type
        @Query("SELECT r.eventType, COUNT(r) FROM Report r GROUP BY r.eventType")
        List<Object[]> countByEventType();

        // Statistics - count by status
        @Query("SELECT r.status, COUNT(r) FROM Report r GROUP BY r.status")
        List<Object[]> countByStatus();

        // Find reports within radius using direct geom field
        @Query(value = "SELECT r.* FROM reports r " +
                        "WHERE ST_DWithin(r.geom::geography, ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography, :radius)", nativeQuery = true)
        List<Report> findWithinRadius(
                        @Param("lon") double longitude,
                        @Param("lat") double latitude,
                        @Param("radius") double radiusMeters);
}
