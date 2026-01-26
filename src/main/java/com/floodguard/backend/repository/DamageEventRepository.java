package com.floodguard.backend.repository;

import com.floodguard.backend.model.DamageEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DamageEventRepository extends JpaRepository<DamageEvent, UUID> {

    List<DamageEvent> findByEventType(String eventType);

    List<DamageEvent> findBySeverityLevel(Integer severityLevel);

    List<DamageEvent> findBySeverityLevelGreaterThanEqual(Integer minLevel);

    @Query("SELECT e FROM DamageEvent e WHERE e.startTime >= :start AND (e.endTime IS NULL OR e.endTime <= :end)")
    List<DamageEvent> findByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT e FROM DamageEvent e WHERE e.endTime IS NULL ORDER BY e.startTime DESC")
    List<DamageEvent> findActiveEvents();

    List<DamageEvent> findAllByOrderByCreatedAtDesc();
}
