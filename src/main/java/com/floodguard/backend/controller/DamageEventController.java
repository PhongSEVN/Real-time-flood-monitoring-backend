package com.floodguard.backend.controller;

import com.floodguard.backend.dto.DamageEventDTO;
import com.floodguard.backend.response.ApiResponse;
import com.floodguard.backend.service.DamageEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DamageEventController {

    private final DamageEventService damageEventService;

    @PostMapping
    public ResponseEntity<ApiResponse<DamageEventDTO.Response>> create(
            @Valid @RequestBody DamageEventDTO.CreateRequest request) {
        DamageEventDTO.Response response = damageEventService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Damage event created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DamageEventDTO.Response>>> getAll() {
        List<DamageEventDTO.Response> events = damageEventService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DamageEventDTO.Response>> getById(@PathVariable UUID id) {
        DamageEventDTO.Response response = damageEventService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Event retrieved successfully", response));
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<ApiResponse<List<DamageEventDTO.Response>>> getByEventType(
            @PathVariable String eventType) {
        List<DamageEventDTO.Response> events = damageEventService.getByEventType(eventType);
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
    }

    @GetMapping("/severity/{level}")
    public ResponseEntity<ApiResponse<List<DamageEventDTO.Response>>> getBySeverityLevel(
            @PathVariable Integer level) {
        List<DamageEventDTO.Response> events = damageEventService.getBySeverityLevel(level);
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DamageEventDTO.Response>>> getActiveEvents() {
        List<DamageEventDTO.Response> events = damageEventService.getActiveEvents();
        return ResponseEntity.ok(ApiResponse.success("Active events retrieved successfully", events));
    }

    @GetMapping("/time-range")
    public ResponseEntity<ApiResponse<List<DamageEventDTO.Response>>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<DamageEventDTO.Response> events = damageEventService.getByTimeRange(start, end);
        return ResponseEntity.ok(ApiResponse.success("Events retrieved successfully", events));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DamageEventDTO.Response>> update(
            @PathVariable UUID id,
            @Valid @RequestBody DamageEventDTO.UpdateRequest request) {
        DamageEventDTO.Response response = damageEventService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        damageEventService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully", null));
    }
}
