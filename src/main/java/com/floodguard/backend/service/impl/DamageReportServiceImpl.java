package com.floodguard.backend.service.impl;

import com.floodguard.backend.dto.DamageAssetDTO;
import com.floodguard.backend.dto.DamageReportDTO;
import com.floodguard.backend.exception.ResourceNotFoundException;
import com.floodguard.backend.model.*;
import com.floodguard.backend.repository.*;
import com.floodguard.backend.service.DamageReportService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DamageReportServiceImpl implements DamageReportService {

    private final DamageReportRepository damageReportRepository;
    private final DamageEventRepository damageEventRepository;
    private final DamageAreaRepository damageAreaRepository;
    private final DamageAssetRepository damageAssetRepository;
    private final UserRepository userRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public DamageReportDTO.Response create(DamageReportDTO.CreateRequest request) {
        DamageEvent event = damageEventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("DamageEvent", "id", request.getEventId()));

        DamageArea area = null;
        if (request.getAreaId() != null) {
            area = damageAreaRepository.findById(request.getAreaId())
                    .orElseThrow(() -> new ResourceNotFoundException("DamageArea", "id", request.getAreaId()));
        }

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        }

        Point point = createPoint(request.getLongitude(), request.getLatitude());

        DamageReport report = DamageReport.builder()
                .user(user)
                .event(event)
                .area(area)
                .reporterName(request.getReporterName())
                .reporterPhone(request.getReporterPhone())
                .damageType(request.getDamageType())
                .damageLevel(request.getDamageLevel())
                .estimatedLoss(request.getEstimatedLoss())
                .description(request.getDescription())
                .geom(point)
                .isVerified(false)
                .build();

        DamageReport saved = damageReportRepository.save(report);

        if (request.getAssets() != null && !request.getAssets().isEmpty()) {
            for (DamageAssetDTO.CreateRequest assetReq : request.getAssets()) {
                DamageAsset asset = DamageAsset.builder()
                        .report(saved)
                        .assetType(assetReq.getAssetType())
                        .assetName(assetReq.getAssetName())
                        .quantity(assetReq.getQuantity())
                        .unit(assetReq.getUnit())
                        .estimatedValue(assetReq.getEstimatedValue())
                        .build();
                damageAssetRepository.save(asset);
            }
        }

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DamageReportDTO.Response getById(UUID id) {
        DamageReport report = damageReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DamageReport", "id", id));
        return toResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageReportDTO.Response> getAll() {
        return damageReportRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageReportDTO.Response> getByEventId(UUID eventId) {
        return damageReportRepository.findByEventId(eventId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageReportDTO.Response> getByAreaId(UUID areaId) {
        return damageReportRepository.findByAreaId(areaId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageReportDTO.Response> getByUserId(UUID userId) {
        return damageReportRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageReportDTO.Response> getByDamageType(String damageType) {
        return damageReportRepository.findByDamageType(damageType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageReportDTO.Response> getUnverifiedReports() {
        return damageReportRepository.findByIsVerified(false)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DamageReportDTO.Response> findWithinRadius(double longitude, double latitude, double radiusMeters) {
        return damageReportRepository.findReportsWithinRadius(longitude, latitude, radiusMeters)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DamageReportDTO.Response update(UUID id, DamageReportDTO.UpdateRequest request) {
        DamageReport report = damageReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DamageReport", "id", id));

        if (request.getReporterName() != null) {
            report.setReporterName(request.getReporterName());
        }
        if (request.getReporterPhone() != null) {
            report.setReporterPhone(request.getReporterPhone());
        }
        if (request.getDamageType() != null) {
            report.setDamageType(request.getDamageType());
        }
        if (request.getDamageLevel() != null) {
            report.setDamageLevel(request.getDamageLevel());
        }
        if (request.getEstimatedLoss() != null) {
            report.setEstimatedLoss(request.getEstimatedLoss());
        }
        if (request.getDescription() != null) {
            report.setDescription(request.getDescription());
        }
        if (request.getLongitude() != null && request.getLatitude() != null) {
            report.setGeom(createPoint(request.getLongitude(), request.getLatitude()));
        }

        DamageReport updated = damageReportRepository.save(report);
        return toResponse(updated);
    }

    @Override
    public DamageReportDTO.Response verify(UUID id, DamageReportDTO.VerifyRequest request) {
        DamageReport report = damageReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DamageReport", "id", id));

        report.setIsVerified(request.getIsVerified());
        DamageReport updated = damageReportRepository.save(report);
        return toResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        if (!damageReportRepository.existsById(id)) {
            throw new ResourceNotFoundException("DamageReport", "id", id);
        }
        damageReportRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCountByDamageType() {
        List<Object[]> results = damageReportRepository.countByDamageType();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] result : results) {
            counts.put((String) result[0], (Long) result[1]);
        }
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalEstimatedLossByEvent(UUID eventId) {
        return damageReportRepository.getTotalEstimatedLossByEvent(eventId);
    }

    private Point createPoint(double longitude, double latitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(4326);
        return point;
    }

    private DamageReportDTO.Response toResponse(DamageReport report) {
        List<DamageAsset> assets = damageAssetRepository.findByReportId(report.getId());
        Long totalAssetValue = damageAssetRepository.getTotalValueByReport(report.getId());

        List<DamageAssetDTO.Response> assetResponses = assets.stream()
                .map(asset -> DamageAssetDTO.Response.builder()
                        .id(asset.getId())
                        .reportId(report.getId())
                        .assetType(asset.getAssetType())
                        .assetName(asset.getAssetName())
                        .quantity(asset.getQuantity())
                        .unit(asset.getUnit())
                        .estimatedValue(asset.getEstimatedValue())
                        .build())
                .collect(Collectors.toList());

        return DamageReportDTO.Response.builder()
                .id(report.getId())
                .userId(report.getUser() != null ? report.getUser().getId() : null)
                .eventId(report.getEvent() != null ? report.getEvent().getId() : null)
                .eventType(report.getEvent() != null ? report.getEvent().getEventType() : null)
                .areaId(report.getArea() != null ? report.getArea().getId() : null)
                .areaName(report.getArea() != null ? report.getArea().getAreaName() : null)
                .reporterName(report.getReporterName())
                .reporterPhone(report.getReporterPhone())
                .damageType(report.getDamageType())
                .damageLevel(report.getDamageLevel())
                .estimatedLoss(report.getEstimatedLoss())
                .description(report.getDescription())
                .longitude(report.getGeom() != null ? report.getGeom().getX() : null)
                .latitude(report.getGeom() != null ? report.getGeom().getY() : null)
                .isVerified(report.getIsVerified())
                .createdAt(report.getCreatedAt())
                .assets(assetResponses)
                .totalAssetValue(totalAssetValue)
                .build();
    }
}
