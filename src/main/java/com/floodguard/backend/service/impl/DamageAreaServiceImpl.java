package com.floodguard.backend.service.impl;

import com.floodguard.backend.dto.DamageAreaDTO;
import com.floodguard.backend.exception.ResourceNotFoundException;
import com.floodguard.backend.model.DamageArea;
import com.floodguard.backend.model.DamageEvent;
import com.floodguard.backend.repository.DamageAreaRepository;
import com.floodguard.backend.repository.DamageEventRepository;
import com.floodguard.backend.service.DamageAreaService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DamageAreaServiceImpl implements DamageAreaService {

    private final DamageAreaRepository damageAreaRepository;
    private final DamageEventRepository damageEventRepository;

    @Override
    public DamageAreaDTO.Response create(DamageAreaDTO.CreateRequest request) {
        DamageEvent event = damageEventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("DamageEvent", "id", request.getEventId()));

        Polygon polygon = parsePolygon(request.getGeomWkt());

        DamageArea area = DamageArea.builder()
                .event(event)
                .areaName(request.getAreaName())
                .geom(polygon)
                .riskLevel(request.getRiskLevel())
                .estimatedHouseholds(request.getEstimatedHouseholds())
                .build();

        DamageArea saved = damageAreaRepository.save(area);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DamageAreaDTO.Response getById(UUID id) {
        DamageArea area = damageAreaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DamageArea", "id", id));
        return toResponse(area);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageAreaDTO.Response> getAll() {
        return damageAreaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageAreaDTO.Response> getByEventId(UUID eventId) {
        return damageAreaRepository.findByEventId(eventId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageAreaDTO.Response> getByRiskLevel(Integer riskLevel) {
        return damageAreaRepository.findByRiskLevelGreaterThanEqual(riskLevel)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageAreaDTO.Response> findAreasContainingPoint(double longitude, double latitude) {
        return damageAreaRepository.findAreasContainingPoint(longitude, latitude)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageAreaDTO.Response> findAreasInBoundingBox(double minLon, double minLat, double maxLon,
            double maxLat) {
        return damageAreaRepository.findAreasInBoundingBox(minLon, minLat, maxLon, maxLat)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DamageAreaDTO.Response update(UUID id, DamageAreaDTO.UpdateRequest request) {
        DamageArea area = damageAreaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DamageArea", "id", id));

        if (request.getAreaName() != null) {
            area.setAreaName(request.getAreaName());
        }
        if (request.getGeomWkt() != null) {
            area.setGeom(parsePolygon(request.getGeomWkt()));
        }
        if (request.getRiskLevel() != null) {
            area.setRiskLevel(request.getRiskLevel());
        }
        if (request.getEstimatedHouseholds() != null) {
            area.setEstimatedHouseholds(request.getEstimatedHouseholds());
        }

        DamageArea updated = damageAreaRepository.save(area);
        return toResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        if (!damageAreaRepository.existsById(id)) {
            throw new ResourceNotFoundException("DamageArea", "id", id);
        }
        damageAreaRepository.deleteById(id);
    }

    private Polygon parsePolygon(String wkt) {
        try {
            WKTReader reader = new WKTReader();
            Polygon polygon = (Polygon) reader.read(wkt);
            polygon.setSRID(4326);
            return polygon;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid WKT polygon format: " + e.getMessage());
        }
    }

    private DamageAreaDTO.Response toResponse(DamageArea area) {
        return DamageAreaDTO.Response.builder()
                .id(area.getId())
                .eventId(area.getEvent() != null ? area.getEvent().getId() : null)
                .eventType(area.getEvent() != null ? area.getEvent().getEventType() : null)
                .areaName(area.getAreaName())
                .geomWkt(area.getGeom() != null ? area.getGeom().toText() : null)
                .riskLevel(area.getRiskLevel())
                .estimatedHouseholds(area.getEstimatedHouseholds())
                .createdAt(area.getCreatedAt())
                .reportsCount(area.getDamageReports() != null ? area.getDamageReports().size() : 0)
                .build();
    }
}
