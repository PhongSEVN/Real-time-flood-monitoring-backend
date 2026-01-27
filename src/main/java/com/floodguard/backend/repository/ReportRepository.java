package com.floodguard.backend.repository;

import com.floodguard.backend.model.Report;
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

    // Find by location's ID (using property path)
    List<Report> findByLocationLocationId(Long locationId);

    List<Report> findByEventType(String eventType);

    List<Report> findByVerifyStatus(String verifyStatus);

    List<Report> findByDamageLevelGreaterThanEqual(Integer damageLevel);

    // Find by time range
    List<Report> findByReportTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT r FROM Report r ORDER BY r.reportTime DESC")
    List<Report> findAllOrderByReportTimeDesc();

    // Find pending reports for verification
    @Query("SELECT r FROM Report r WHERE r.verifyStatus = 'PENDING' ORDER BY r.reportTime DESC")
    List<Report> findPendingReports();

    // Statistics - count by event type
    @Query("SELECT r.eventType, COUNT(r) FROM Report r GROUP BY r.eventType")
    List<Object[]> countByEventType();

    // Statistics - count by verify status
    @Query("SELECT r.verifyStatus, COUNT(r) FROM Report r GROUP BY r.verifyStatus")
    List<Object[]> countByVerifyStatus();

    // Find reports with location within radius
    @Query(value = "SELECT r.* FROM reports r JOIN locations l ON r.location_id = l.location_id " +
            "WHERE ST_DWithin(l.coordinates::geography, ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography, :radius)", nativeQuery = true)
    List<Report> findWithinRadius(
            @Param("lon") double longitude,
            @Param("lat") double latitude,
            @Param("radius") double radiusMeters);
}
