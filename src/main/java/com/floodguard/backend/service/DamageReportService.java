package com.floodguard.backend.service;

import com.floodguard.backend.dto.DamageReportDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DamageReportService {

    DamageReportDTO.Response create(DamageReportDTO.CreateRequest request);

    DamageReportDTO.Response getById(UUID id);

    List<DamageReportDTO.Response> getAll();

    List<DamageReportDTO.Response> getByEventId(UUID eventId);

    List<DamageReportDTO.Response> getByAreaId(UUID areaId);

    List<DamageReportDTO.Response> getByUserId(UUID userId);

    List<DamageReportDTO.Response> getByDamageType(String damageType);

    List<DamageReportDTO.Response> getUnverifiedReports();

    List<DamageReportDTO.Response> findWithinRadius(double longitude, double latitude, double radiusMeters);

    DamageReportDTO.Response update(UUID id, DamageReportDTO.UpdateRequest request);

    DamageReportDTO.Response verify(UUID id, DamageReportDTO.VerifyRequest request);

    void delete(UUID id);

    // Statistics
    Map<String, Long> getCountByDamageType();

    Long getTotalEstimatedLossByEvent(UUID eventId);
}
