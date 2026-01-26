package com.floodguard.backend.controller;

import com.floodguard.backend.dto.DamageAreaDTO;
import com.floodguard.backend.response.ApiResponse;
import com.floodguard.backend.service.DamageAreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DamageAreaController {

    private final DamageAreaService damageAreaService;

    @PostMapping
    public ResponseEntity<ApiResponse<DamageAreaDTO.Response>> create(
            @Valid @RequestBody DamageAreaDTO.CreateRequest request) {
        DamageAreaDTO.Response response = damageAreaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Damage area created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DamageAreaDTO.Response>>> getAll() {
        List<DamageAreaDTO.Response> areas = damageAreaService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Areas retrieved successfully", areas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DamageAreaDTO.Response>> getById(@PathVariable UUID id) {
        DamageAreaDTO.Response response = damageAreaService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Area retrieved successfully", response));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<DamageAreaDTO.Response>>> getByEventId(
            @PathVariable UUID eventId) {
        List<DamageAreaDTO.Response> areas = damageAreaService.getByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Areas retrieved successfully", areas));
    }

    @GetMapping("/risk-level/{level}")
    public ResponseEntity<ApiResponse<List<DamageAreaDTO.Response>>> getByRiskLevel(
            @PathVariable Integer level) {
        List<DamageAreaDTO.Response> areas = damageAreaService.getByRiskLevel(level);
        return ResponseEntity.ok(ApiResponse.success("Areas retrieved successfully", areas));
    }

    @GetMapping("/containing-point")
    public ResponseEntity<ApiResponse<List<DamageAreaDTO.Response>>> findAreasContainingPoint(
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        List<DamageAreaDTO.Response> areas = damageAreaService.findAreasContainingPoint(longitude, latitude);
        return ResponseEntity.ok(ApiResponse.success("Areas containing point retrieved successfully", areas));
    }

    @GetMapping("/bounding-box")
    public ResponseEntity<ApiResponse<List<DamageAreaDTO.Response>>> findAreasInBoundingBox(
            @RequestParam Double minLon,
            @RequestParam Double minLat,
            @RequestParam Double maxLon,
            @RequestParam Double maxLat) {
        List<DamageAreaDTO.Response> areas = damageAreaService.findAreasInBoundingBox(minLon, minLat, maxLon, maxLat);
        return ResponseEntity.ok(ApiResponse.success("Areas in bounding box retrieved successfully", areas));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DamageAreaDTO.Response>> update(
            @PathVariable UUID id,
            @Valid @RequestBody DamageAreaDTO.UpdateRequest request) {
        DamageAreaDTO.Response response = damageAreaService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Area updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        damageAreaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Area deleted successfully", null));
    }
}
