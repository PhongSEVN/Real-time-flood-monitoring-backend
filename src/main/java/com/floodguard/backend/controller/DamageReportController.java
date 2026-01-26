package com.floodguard.backend.controller;

import com.floodguard.backend.dto.DamageReportDTO;
import com.floodguard.backend.response.ApiResponse;
import com.floodguard.backend.service.DamageReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DamageReportController {

    private final DamageReportService damageReportService;

    @PostMapping
    public ResponseEntity<ApiResponse<DamageReportDTO.Response>> create(
            @Valid @RequestBody DamageReportDTO.CreateRequest request) {
        DamageReportDTO.Response response = damageReportService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Damage report created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DamageReportDTO.Response>>> getAll() {
        List<DamageReportDTO.Response> reports = damageReportService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", reports));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DamageReportDTO.Response>> getById(@PathVariable UUID id) {
        DamageReportDTO.Response response = damageReportService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Report retrieved successfully", response));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<DamageReportDTO.Response>>> getByEventId(
            @PathVariable UUID eventId) {
        List<DamageReportDTO.Response> reports = damageReportService.getByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", reports));
    }

    @GetMapping("/area/{areaId}")
    public ResponseEntity<ApiResponse<List<DamageReportDTO.Response>>> getByAreaId(
            @PathVariable UUID areaId) {
        List<DamageReportDTO.Response> reports = damageReportService.getByAreaId(areaId);
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", reports));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<DamageReportDTO.Response>>> getByUserId(
            @PathVariable UUID userId) {
        List<DamageReportDTO.Response> reports = damageReportService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", reports));
    }

    @GetMapping("/type/{damageType}")
    public ResponseEntity<ApiResponse<List<DamageReportDTO.Response>>> getByDamageType(
            @PathVariable String damageType) {
        List<DamageReportDTO.Response> reports = damageReportService.getByDamageType(damageType);
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", reports));
    }

    @GetMapping("/unverified")
    public ResponseEntity<ApiResponse<List<DamageReportDTO.Response>>> getUnverifiedReports() {
        List<DamageReportDTO.Response> reports = damageReportService.getUnverifiedReports();
        return ResponseEntity.ok(ApiResponse.success("Unverified reports retrieved successfully", reports));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<DamageReportDTO.Response>>> findWithinRadius(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(defaultValue = "1000") Double radius) {
        List<DamageReportDTO.Response> reports = damageReportService.findWithinRadius(longitude, latitude, radius);
        return ResponseEntity.ok(ApiResponse.success("Nearby reports retrieved successfully", reports));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DamageReportDTO.Response>> update(
            @PathVariable UUID id,
            @Valid @RequestBody DamageReportDTO.UpdateRequest request) {
        DamageReportDTO.Response response = damageReportService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Report updated successfully", response));
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<DamageReportDTO.Response>> verify(
            @PathVariable UUID id,
            @Valid @RequestBody DamageReportDTO.VerifyRequest request) {
        DamageReportDTO.Response response = damageReportService.verify(id, request);
        return ResponseEntity.ok(ApiResponse.success("Report verification status updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        damageReportService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Report deleted successfully", null));
    }

    @GetMapping("/statistics/by-type")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCountByDamageType() {
        Map<String, Long> stats = damageReportService.getCountByDamageType();
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
    }

    @GetMapping("/statistics/total-loss/{eventId}")
    public ResponseEntity<ApiResponse<Long>> getTotalLossByEvent(@PathVariable UUID eventId) {
        Long totalLoss = damageReportService.getTotalEstimatedLossByEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("Total estimated loss retrieved successfully", totalLoss));
    }
}
