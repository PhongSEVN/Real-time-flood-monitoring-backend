package com.floodguard.backend.service;

import com.floodguard.backend.dto.DamageEventDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DamageEventService {

    DamageEventDTO.Response create(DamageEventDTO.CreateRequest request);

    DamageEventDTO.Response getById(UUID id);

    List<DamageEventDTO.Response> getAll();

    List<DamageEventDTO.Response> getByEventType(String eventType);

    List<DamageEventDTO.Response> getBySeverityLevel(Integer level);

    List<DamageEventDTO.Response> getActiveEvents();

    List<DamageEventDTO.Response> getByTimeRange(LocalDateTime start, LocalDateTime end);

    DamageEventDTO.Response update(UUID id, DamageEventDTO.UpdateRequest request);

    void delete(UUID id);
}
