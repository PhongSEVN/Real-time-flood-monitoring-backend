package com.floodguard.backend.controller;

import com.floodguard.backend.dto.LocationDTO;
import com.floodguard.backend.response.ApiResponse;
import com.floodguard.backend.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<ApiResponse<LocationDTO.Response>> create(
            @Valid @RequestBody LocationDTO.CreateRequest request) {
        LocationDTO.Response response = locationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Location created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO.Response>> getById(@PathVariable Long id) {
        LocationDTO.Response response = locationService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Location retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationDTO.Response>>> getAll() {
        List<LocationDTO.Response> responses = locationService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Locations retrieved successfully", responses));
    }

    @GetMapping("/type/{locationType}")
    public ResponseEntity<ApiResponse<List<LocationDTO.Response>>> getByLocationType(
            @PathVariable String locationType) {
        List<LocationDTO.Response> responses = locationService.getByLocationType(locationType);
        return ResponseEntity.ok(ApiResponse.success("Locations retrieved successfully", responses));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<LocationDTO.Response>>> findWithinRadius(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "1000") double radiusMeters) {
        List<LocationDTO.Response> responses = locationService.findWithinRadius(longitude, latitude, radiusMeters);
        return ResponseEntity.ok(ApiResponse.success("Nearby locations retrieved successfully", responses));
    }

    @GetMapping("/in-bounds")
    public ResponseEntity<ApiResponse<List<LocationDTO.Response>>> findInBoundingBox(
            @RequestParam double minLon,
            @RequestParam double minLat,
            @RequestParam double maxLon,
            @RequestParam double maxLat) {
        List<LocationDTO.Response> responses = locationService.findInBoundingBox(minLon, minLat, maxLon, maxLat);
        return ResponseEntity.ok(ApiResponse.success("Locations in bounding box retrieved successfully", responses));
    }

    @GetMapping("/nearest")
    public ResponseEntity<ApiResponse<List<LocationDTO.Response>>> findNearest(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "10") int limit) {
        List<LocationDTO.Response> responses = locationService.findNearest(longitude, latitude, limit);
        return ResponseEntity.ok(ApiResponse.success("Nearest locations retrieved successfully", responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO.Response>> update(
            @PathVariable Long id,
            @Valid @RequestBody LocationDTO.UpdateRequest request) {
        LocationDTO.Response response = locationService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Location updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Location deleted successfully", null));
    }
}
