package com.floodguard.backend.service;

import com.floodguard.backend.dto.LocationDTO;

import java.util.List;

public interface LocationService {

    LocationDTO.Response create(LocationDTO.CreateRequest request);

    LocationDTO.Response getById(Long id);

    List<LocationDTO.Response> getAll();

    List<LocationDTO.Response> getByLocationType(String locationType);

    List<LocationDTO.Response> findWithinRadius(double longitude, double latitude, double radiusMeters);

    List<LocationDTO.Response> findInBoundingBox(double minLon, double minLat, double maxLon, double maxLat);

    List<LocationDTO.Response> findNearest(double longitude, double latitude, int limit);

    LocationDTO.Response update(Long id, LocationDTO.UpdateRequest request);

    void delete(Long id);
}
