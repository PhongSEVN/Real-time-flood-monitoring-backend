package com.floodguard.backend.controller;

import com.floodguard.backend.dto.ReportDTO;
import com.floodguard.backend.response.ApiResponse;
import com.floodguard.backend.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReportDTO.Response>> create(
            @Valid @RequestBody ReportDTO.CreateRequest request) {
        ReportDTO.Response response = reportService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Report created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO.Response>> getById(@PathVariable Long id) {
        ReportDTO.Response response = reportService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Report retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReportDTO.Response>>> getAll() {
        List<ReportDTO.Response> responses = reportService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", responses));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReportDTO.Response>>> getByUserId(@PathVariable Long userId) {
        List<ReportDTO.Response> responses = reportService.getByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", responses));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<ApiResponse<List<ReportDTO.Response>>> getByLocationId(@PathVariable Long locationId) {
        List<ReportDTO.Response> responses = reportService.getByLocationId(locationId);
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", responses));
    }

    @GetMapping("/event-type/{eventType}")
    public ResponseEntity<ApiResponse<List<ReportDTO.Response>>> getByEventType(@PathVariable String eventType) {
        List<ReportDTO.Response> responses = reportService.getByEventType(eventType);
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", responses));
    }

    @GetMapping("/status/{verifyStatus}")
    public ResponseEntity<ApiResponse<List<ReportDTO.Response>>> getByVerifyStatus(@PathVariable String verifyStatus) {
        List<ReportDTO.Response> responses = reportService.getByVerifyStatus(verifyStatus);
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", responses));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ReportDTO.Response>>> getPendingReports() {
        List<ReportDTO.Response> responses = reportService.getPendingReports();
        return ResponseEntity.ok(ApiResponse.success("Pending reports retrieved successfully", responses));
    }

    @GetMapping("/time-range")
    public ResponseEntity<ApiResponse<List<ReportDTO.Response>>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ReportDTO.Response> responses = reportService.getByTimeRange(start, end);
        return ResponseEntity.ok(ApiResponse.success("Reports retrieved successfully", responses));
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<ReportDTO.Response>>> findWithinRadius(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "1000") double radiusMeters) {
        List<ReportDTO.Response> responses = reportService.findWithinRadius(longitude, latitude, radiusMeters);
        return ResponseEntity.ok(ApiResponse.success("Nearby reports retrieved successfully", responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO.Response>> update(
            @PathVariable Long id,
            @Valid @RequestBody ReportDTO.UpdateRequest request) {
        ReportDTO.Response response = reportService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Report updated successfully", response));
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<ReportDTO.Response>> verify(
            @PathVariable Long id,
            @Valid @RequestBody ReportDTO.VerifyRequest request) {
        ReportDTO.Response response = reportService.verify(id, request);
        return ResponseEntity.ok(ApiResponse.success("Report verification status updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reportService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Report deleted successfully", null));
    }

    // Statistics endpoints
    @GetMapping("/statistics/by-event-type")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCountByEventType() {
        Map<String, Long> stats = reportService.getCountByEventType();
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
    }

    @GetMapping("/statistics/by-status")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCountByVerifyStatus() {
        Map<String, Long> stats = reportService.getCountByVerifyStatus();
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
    }
}
