package com.floodguard.backend.controller;

import com.floodguard.backend.dto.HistoricalDataDTO;
import com.floodguard.backend.response.ApiResponse;
import com.floodguard.backend.service.HistoricalDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historical-data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HistoricalDataController {

    private final HistoricalDataService historicalDataService;

    @PostMapping
    public ResponseEntity<ApiResponse<HistoricalDataDTO.Response>> create(
            @Valid @RequestBody HistoricalDataDTO.CreateRequest request) {
        HistoricalDataDTO.Response response = historicalDataService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Historical data created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HistoricalDataDTO.Response>> getById(@PathVariable Long id) {
        HistoricalDataDTO.Response response = historicalDataService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Historical data retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HistoricalDataDTO.Response>>> getAll() {
        List<HistoricalDataDTO.Response> responses = historicalDataService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Historical data retrieved successfully", responses));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<HistoricalDataDTO.Response>>> getByYear(@PathVariable Integer year) {
        List<HistoricalDataDTO.Response> responses = historicalDataService.getByYear(year);
        return ResponseEntity.ok(ApiResponse.success("Historical data retrieved successfully", responses));
    }

    @GetMapping("/alert-level/{alertLevel}")
    public ResponseEntity<ApiResponse<List<HistoricalDataDTO.Response>>> getByAlertLevel(
            @PathVariable String alertLevel) {
        List<HistoricalDataDTO.Response> responses = historicalDataService.getByAlertLevel(alertLevel);
        return ResponseEntity.ok(ApiResponse.success("Historical data retrieved successfully", responses));
    }

    @GetMapping("/year-range")
    public ResponseEntity<ApiResponse<List<HistoricalDataDTO.Response>>> getByYearRange(
            @RequestParam Integer startYear,
            @RequestParam Integer endYear) {
        List<HistoricalDataDTO.Response> responses = historicalDataService.getByYearRange(startYear, endYear);
        return ResponseEntity.ok(ApiResponse.success("Historical data retrieved successfully", responses));
    }

    @GetMapping("/containing-point")
    public ResponseEntity<ApiResponse<List<HistoricalDataDTO.Response>>> findContainingPoint(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        List<HistoricalDataDTO.Response> responses = historicalDataService.findContainingPoint(longitude, latitude);
        return ResponseEntity
                .ok(ApiResponse.success("Historical data containing point retrieved successfully", responses));
    }

    @GetMapping("/in-bounds")
    public ResponseEntity<ApiResponse<List<HistoricalDataDTO.Response>>> findInBoundingBox(
            @RequestParam double minLon,
            @RequestParam double minLat,
            @RequestParam double maxLon,
            @RequestParam double maxLat) {
        List<HistoricalDataDTO.Response> responses = historicalDataService.findInBoundingBox(minLon, minLat, maxLon,
                maxLat);
        return ResponseEntity
                .ok(ApiResponse.success("Historical data in bounding box retrieved successfully", responses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HistoricalDataDTO.Response>> update(
            @PathVariable Long id,
            @Valid @RequestBody HistoricalDataDTO.UpdateRequest request) {
        HistoricalDataDTO.Response response = historicalDataService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Historical data updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        historicalDataService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Historical data deleted successfully", null));
    }
}
