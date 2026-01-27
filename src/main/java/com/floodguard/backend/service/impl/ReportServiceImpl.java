package com.floodguard.backend.service.impl;

import com.floodguard.backend.dto.ReportDTO;
import com.floodguard.backend.exception.ResourceNotFoundException;
import com.floodguard.backend.model.Location;
import com.floodguard.backend.model.Report;
import com.floodguard.backend.model.User;
import com.floodguard.backend.repository.LocationRepository;
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
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public ReportDTO.Response create(ReportDTO.CreateRequest request) {
        Location location;

        // Nếu có locationId thì dùng location có sẵn, ngược lại tạo mới
        if (request.getLocationId() != null) {
            location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location", "id", request.getLocationId()));
        } else if (request.getLongitude() != null && request.getLatitude() != null) {
            // Tạo location mới
            location = Location.builder()
                    .address(request.getAddress())
                    .geom(createPoint(request.getLongitude(), request.getLatitude()))
                    .build();
            location = locationRepository.save(location);
        } else {
            throw new IllegalArgumentException(
                    "Either locationId or coordinates (longitude, latitude) must be provided");
        }

        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        }

        Report report = Report.builder()
                .location(location)
                .user(user)
                .reportTime(LocalDateTime.now())
                .eventType(request.getEventType())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .damageLevel(request.getDamageLevel())
                .verifyStatus("PENDING")
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
        return reportRepository.findByLocationLocationId(locationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
        return reportRepository.findByVerifyStatus(verifyStatus).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> getPendingReports() {
        return reportRepository.findByVerifyStatus("PENDING").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDTO.Response> getByTimeRange(LocalDateTime start, LocalDateTime end) {
        return reportRepository.findByReportTimeBetween(start, end).stream()
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

        report.setVerifyStatus(request.getVerifyStatus());
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
        List<Object[]> results = reportRepository.countByVerifyStatus();
        Map<String, Long> counts = new HashMap<>();
        for (Object[] row : results) {
            counts.put((String) row[0], (Long) row[1]);
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
        String locationAddress = null;
        Long locationId = null;

        if (report.getLocation() != null) {
            locationId = report.getLocation().getLocationId();
            locationAddress = report.getLocation().getAddress();
            if (report.getLocation().getGeom() != null) {
                longitude = report.getLocation().getGeom().getX();
                latitude = report.getLocation().getGeom().getY();
            }
        }

        return ReportDTO.Response.builder()
                .reportId(report.getReportId())
                .locationId(locationId)
                .locationAddress(locationAddress)
                .longitude(longitude)
                .latitude(latitude)
                .userId(report.getUser() != null ? report.getUser().getUserId() : null)
                .userFullName(report.getUser() != null ? report.getUser().getFullName() : null)
                .reportTime(report.getReportTime())
                .eventType(report.getEventType())
                .description(report.getDescription())
                .imageUrl(report.getImageUrl())
                .damageLevel(report.getDamageLevel())
                .verifyStatus(report.getVerifyStatus())
                .build();
    }
}
