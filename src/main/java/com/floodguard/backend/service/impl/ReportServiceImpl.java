package com.floodguard.backend.service.impl;

import com.floodguard.backend.dto.ReportDTO;
import com.floodguard.backend.exception.ResourceNotFoundException;
import com.floodguard.backend.model.Report;
import com.floodguard.backend.model.ReportStatus;
import com.floodguard.backend.model.User;
import com.floodguard.backend.repository.ReportRepository;
import com.floodguard.backend.repository.UserRepository;
import com.floodguard.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public ReportDTO.Response create(ReportDTO.CreateRequest request) {
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        }

        Point geom = null;
        if (request.getLongitude() != null && request.getLatitude() != null) {
            geom = createPoint(request.getLongitude(), request.getLatitude());
        }

        Report report = Report.builder()
                .user(user)
                .guestName(request.getGuestName())
                .guestPhone(request.getGuestPhone())
                .eventType(request.getEventType())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .damageLevel(request.getDamageLevel() != null ? request.getDamageLevel() : 0)
                .status(ReportStatus.UNVERIFIED)
                .geom(geom)
                .build();

        Report saved = reportRepository.save(report);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDTO.Response getById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));
        return mapToResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> getAll() {
        return reportRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> getByUserId(Long userId) {
        return reportRepository.findByUserUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> getByLocationId(Long locationId) {
        // Location-based query removed in new schema - return empty list
        return List.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> getByEventType(String eventType) {
        return reportRepository.findByEventType(eventType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> getByVerifyStatus(String verifyStatus) {
        try {
            ReportStatus status = ReportStatus.valueOf(verifyStatus.toUpperCase());
            return reportRepository.findByStatus(status).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> getPendingReports() {
        return reportRepository.findByStatus(ReportStatus.UNVERIFIED).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> getByTimeRange(LocalDateTime start, LocalDateTime end) {
        return reportRepository.findByCreatedAtBetween(start, end).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> findWithinRadius(double longitude, double latitude, double radiusMeters) {
        return reportRepository.findWithinRadius(longitude, latitude, radiusMeters).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReportDTO.Response update(Long id, ReportDTO.UpdateRequest request) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));

        if (request.getEventType() != null) {
            report.setEventType(request.getEventType());
        }
        if (request.getDescription() != null) {
            report.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            report.setImageUrl(request.getImageUrl());
        }
        if (request.getDamageLevel() != null) {
            report.setDamageLevel(request.getDamageLevel());
        }

        Report updated = reportRepository.save(report);
        return mapToResponse(updated);
    }

    @Override
    public ReportDTO.Response verify(Long id, ReportDTO.VerifyRequest request) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));

        try {
            ReportStatus newStatus = ReportStatus.valueOf(request.getVerifyStatus().toUpperCase());
            report.setStatus(newStatus);
            report.setVerifiedAt(LocalDateTime.now());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + request.getVerifyStatus());
        }

        Report updated = reportRepository.save(report);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Report", "id", id);
        }
        reportRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCountByEventType() {
        List<Object[]> results = reportRepository.countByEventType();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : results) {
            counts.put((String) row[0], (Long) row[1]);
        }
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCountByVerifyStatus() {
        List<Object[]> results = reportRepository.countByStatus();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : results) {
            ReportStatus status = (ReportStatus) row[0];
            counts.put(status.name(), (Long) row[1]);
        }
        return counts;
    }

    private Point createPoint(double longitude, double latitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(4326);
        return point;
    }

    private ReportDTO.Response mapToResponse(Report report) {
        Double longitude = null;
        Double latitude = null;

        if (report.getGeom() != null) {
            longitude = report.getGeom().getX();
            latitude = report.getGeom().getY();
        }

        return ReportDTO.Response.builder()
                .reportId(report.getReportId())
                .locationId(null)
                .locationAddress(null)
                .longitude(longitude)
                .latitude(latitude)
                .userId(report.getUser() != null ? report.getUser().getUserId() : null)
                .userFullName(report.getUser() != null ? report.getUser().getFullName() : null)
                .reportTime(report.getCreatedAt())
                .eventType(report.getEventType())
                .description(report.getDescription())
                .imageUrl(report.getImageUrl())
                .damageLevel(report.getDamageLevel())
                .verifyStatus(report.getStatus().name())
                .build();
    }
}
