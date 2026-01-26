package com.floodguard.backend.service.impl;

import com.floodguard.backend.dto.DamageEventDTO;
import com.floodguard.backend.exception.ResourceNotFoundException;
import com.floodguard.backend.model.DamageEvent;
import com.floodguard.backend.repository.DamageEventRepository;
import com.floodguard.backend.service.DamageEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DamageEventServiceImpl implements DamageEventService {

    private final DamageEventRepository damageEventRepository;

    @Override
    public DamageEventDTO.Response create(DamageEventDTO.CreateRequest request) {
        DamageEvent event = DamageEvent.builder()
                .eventType(request.getEventType())
                .description(request.getDescription())
                .startTime(request.getStartTime() != null ? request.getStartTime() : LocalDateTime.now())
                .endTime(request.getEndTime())
                .severityLevel(request.getSeverityLevel())
                .build();

        DamageEvent saved = damageEventRepository.save(event);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DamageEventDTO.Response getById(UUID id) {
        DamageEvent event = damageEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DamageEvent", "id", id));
        return toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageEventDTO.Response> getAll() {
        return damageEventRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageEventDTO.Response> getByEventType(String eventType) {
        return damageEventRepository.findByEventType(eventType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageEventDTO.Response> getBySeverityLevel(Integer level) {
        return damageEventRepository.findBySeverityLevelGreaterThanEqual(level)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageEventDTO.Response> getActiveEvents() {
        return damageEventRepository.findActiveEvents()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageEventDTO.Response> getByTimeRange(LocalDateTime start, LocalDateTime end) {
        return damageEventRepository.findByTimeRange(start, end)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DamageEventDTO.Response update(UUID id, DamageEventDTO.UpdateRequest request) {
        DamageEvent event = damageEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DamageEvent", "id", id));

        if (request.getEventType() != null) {
            event.setEventType(request.getEventType());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getStartTime() != null) {
            event.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            event.setEndTime(request.getEndTime());
        }
        if (request.getSeverityLevel() != null) {
            event.setSeverityLevel(request.getSeverityLevel());
        }

        DamageEvent updated = damageEventRepository.save(event);
        return toResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        if (!damageEventRepository.existsById(id)) {
            throw new ResourceNotFoundException("DamageEvent", "id", id);
        }
        damageEventRepository.deleteById(id);
    }

    private DamageEventDTO.Response toResponse(DamageEvent event) {
        return DamageEventDTO.Response.builder()
                .id(event.getId())
                .eventType(event.getEventType())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .severityLevel(event.getSeverityLevel())
                .createdAt(event.getCreatedAt())
                .areasCount(event.getDamageAreas() != null ? event.getDamageAreas().size() : 0)
                .reportsCount(event.getDamageReports() != null ? event.getDamageReports().size() : 0)
                .build();
    }
}
