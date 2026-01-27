package com.floodguard.backend.service.impl;

import com.floodguard.backend.dto.LocationDTO;
import com.floodguard.backend.exception.ResourceNotFoundException;
import com.floodguard.backend.model.Location;
import com.floodguard.backend.repository.LocationRepository;
import com.floodguard.backend.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public LocationDTO.Response create(LocationDTO.CreateRequest request) {
        Location location = Location.builder()
                .address(request.getAddress())
                .geom(createPoint(request.getLongitude(), request.getLatitude()))
                .locationType(request.getLocationType())
                .baseElevation(request.getBaseElevation())
                .build();

        Location saved = locationRepository.save(location);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationDTO.Response getById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", id));
        return mapToResponse(location);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO.Response> getAll() {
        return locationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO.Response> getByLocationType(String locationType) {
        return locationRepository.findByLocationType(locationType).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO.Response> findWithinRadius(double longitude, double latitude, double radiusMeters) {
        return locationRepository.findLocationsWithinRadius(longitude, latitude, radiusMeters).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO.Response> findInBoundingBox(double minLon, double minLat, double maxLon, double maxLat) {
        return locationRepository.findLocationsInBoundingBox(minLon, minLat, maxLon, maxLat).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDTO.Response> findNearest(double longitude, double latitude, int limit) {
        return locationRepository.findNearestLocations(longitude, latitude, limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LocationDTO.Response update(Long id, LocationDTO.UpdateRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", id));

        if (request.getAddress() != null) {
            location.setAddress(request.getAddress());
        }
        if (request.getLongitude() != null && request.getLatitude() != null) {
            location.setGeom(createPoint(request.getLongitude(), request.getLatitude()));
        }
        if (request.getLocationType() != null) {
            location.setLocationType(request.getLocationType());
        }
        if (request.getBaseElevation() != null) {
            location.setBaseElevation(request.getBaseElevation());
        }

        Location updated = locationRepository.save(location);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Location", "id", id);
        }
        locationRepository.deleteById(id);
    }

    private Point createPoint(double longitude, double latitude) {
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(4326);
        return point;
    }

    private LocationDTO.Response mapToResponse(Location location) {
        Double longitude = null;
        Double latitude = null;
        if (location.getGeom() != null) {
            longitude = location.getGeom().getX();
            latitude = location.getGeom().getY();
        }

        return LocationDTO.Response.builder()
                .locationId(location.getLocationId())
                .address(location.getAddress())
                .longitude(longitude)
                .latitude(latitude)
                .locationType(location.getLocationType())
                .baseElevation(location.getBaseElevation())
                .reportsCount(location.getReports() != null ? location.getReports().size() : 0)
                .build();
    }
}
