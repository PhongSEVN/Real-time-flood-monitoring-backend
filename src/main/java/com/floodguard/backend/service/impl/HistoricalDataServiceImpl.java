package com.floodguard.backend.service.impl;

import com.floodguard.backend.dto.HistoricalDataDTO;
import com.floodguard.backend.exception.ResourceNotFoundException;
import com.floodguard.backend.model.HistoricalData;
import com.floodguard.backend.repository.HistoricalDataRepository;
import com.floodguard.backend.service.HistoricalDataService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoricalDataServiceImpl implements HistoricalDataService {

    private final HistoricalDataRepository historicalDataRepository;
    private final WKTReader wktReader = new WKTReader();
    private final WKTWriter wktWriter = new WKTWriter();

    @Override
    public HistoricalDataDTO.Response create(HistoricalDataDTO.CreateRequest request) {
        HistoricalData historicalData = HistoricalData.builder()
                .year(request.getYear())
                .alertLevel(request.getAlertLevel())
                .impactSummary(request.getImpactSummary())
                .referenceGeom(parsePolygon(request.getReferenceGeomWkt()))
                .build();

        HistoricalData saved = historicalDataRepository.save(historicalData);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoricalDataDTO.Response getById(Long id) {
        HistoricalData data = historicalDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HistoricalData", "id", id));
        return mapToResponse(data);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricalDataDTO.Response> getAll() {
        return historicalDataRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricalDataDTO.Response> getByYear(Integer year) {
        return historicalDataRepository.findByYear(year).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricalDataDTO.Response> getByAlertLevel(String alertLevel) {
        return historicalDataRepository.findByAlertLevel(alertLevel).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricalDataDTO.Response> getByYearRange(Integer startYear, Integer endYear) {
        return historicalDataRepository.findByYearBetween(startYear, endYear).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricalDataDTO.Response> findContainingPoint(double longitude, double latitude) {
        return historicalDataRepository.findHistoryContainingPoint(longitude, latitude).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoricalDataDTO.Response> findInBoundingBox(double minLon, double minLat, double maxLon,
            double maxLat) {
        return historicalDataRepository.findHistoryInBoundingBox(minLon, minLat, maxLon, maxLat).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HistoricalDataDTO.Response update(Long id, HistoricalDataDTO.UpdateRequest request) {
        HistoricalData data = historicalDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HistoricalData", "id", id));

        if (request.getYear() != null) {
            data.setYear(request.getYear());
        }
        if (request.getAlertLevel() != null) {
            data.setAlertLevel(request.getAlertLevel());
        }
        if (request.getImpactSummary() != null) {
            data.setImpactSummary(request.getImpactSummary());
        }
        if (request.getReferenceGeomWkt() != null) {
            data.setReferenceGeom(parsePolygon(request.getReferenceGeomWkt()));
        }

        HistoricalData updated = historicalDataRepository.save(data);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!historicalDataRepository.existsById(id)) {
            throw new ResourceNotFoundException("HistoricalData", "id", id);
        }
        historicalDataRepository.deleteById(id);
    }

    private Polygon parsePolygon(String wkt) {
        if (wkt == null || wkt.isEmpty()) {
            return null;
        }
        try {
            Geometry geometry = wktReader.read(wkt);
            if (geometry instanceof Polygon polygon) {
                polygon.setSRID(4326);
                return polygon;
            }
            throw new IllegalArgumentException("WKT must represent a Polygon geometry");
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid WKT format: " + e.getMessage());
        }
    }

    private HistoricalDataDTO.Response mapToResponse(HistoricalData data) {
        String wkt = null;
        if (data.getReferenceGeom() != null) {
            wkt = wktWriter.write(data.getReferenceGeom());
        }

        return HistoricalDataDTO.Response.builder()
                .historyId(data.getHistoryId())
                .year(data.getYear())
                .alertLevel(data.getAlertLevel())
                .impactSummary(data.getImpactSummary())
                .referenceGeomWkt(wkt)
                .build();
    }
}
