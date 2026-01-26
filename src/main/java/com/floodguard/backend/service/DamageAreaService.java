package com.floodguard.backend.service;

import com.floodguard.backend.dto.DamageAreaDTO;

import java.util.List;
import java.util.UUID;

public interface DamageAreaService {

    DamageAreaDTO.Response create(DamageAreaDTO.CreateRequest request);

    DamageAreaDTO.Response getById(UUID id);

    List<DamageAreaDTO.Response> getAll();

    List<DamageAreaDTO.Response> getByEventId(UUID eventId);

    List<DamageAreaDTO.Response> getByRiskLevel(Integer riskLevel);

    List<DamageAreaDTO.Response> findAreasContainingPoint(double longitude, double latitude);

    List<DamageAreaDTO.Response> findAreasInBoundingBox(double minLon, double minLat, double maxLon, double maxLat);

    DamageAreaDTO.Response update(UUID id, DamageAreaDTO.UpdateRequest request);

    void delete(UUID id);
}
