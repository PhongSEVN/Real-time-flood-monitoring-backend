package com.floodguard.backend.service;

import com.floodguard.backend.dto.ReportDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {

    ReportDTO.Response create(ReportDTO.CreateRequest request);

    ReportDTO.Response getById(Long id);

    List<ReportDTO.Response> getAll();

    List<ReportDTO.Response> getByUserId(Long userId);

    List<ReportDTO.Response> getByLocationId(Long locationId);

    List<ReportDTO.Response> getByEventType(String eventType);

    List<ReportDTO.Response> getByVerifyStatus(String verifyStatus);

    List<ReportDTO.Response> getPendingReports();

    List<ReportDTO.Response> getByTimeRange(LocalDateTime start, LocalDateTime end);

    List<ReportDTO.Response> findWithinRadius(double longitude, double latitude, double radiusMeters);

    ReportDTO.Response update(Long id, ReportDTO.UpdateRequest request);

    ReportDTO.Response verify(Long id, ReportDTO.VerifyRequest request);

    void delete(Long id);

    // Statistics
    Map<String, Long> getCountByEventType();

    Map<String, Long> getCountByVerifyStatus();
}
