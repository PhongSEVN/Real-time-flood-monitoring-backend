package com.floodguard.backend.service;

import com.floodguard.backend.dto.HistoricalDataDTO;

import java.util.List;

public interface HistoricalDataService {

    HistoricalDataDTO.Response create(HistoricalDataDTO.CreateRequest request);

    HistoricalDataDTO.Response getById(Long id);

    List<HistoricalDataDTO.Response> getAll();

    List<HistoricalDataDTO.Response> getByYear(Integer year);

    List<HistoricalDataDTO.Response> getByAlertLevel(String alertLevel);

    List<HistoricalDataDTO.Response> getByYearRange(Integer startYear, Integer endYear);

    List<HistoricalDataDTO.Response> findContainingPoint(double longitude, double latitude);

    List<HistoricalDataDTO.Response> findInBoundingBox(double minLon, double minLat, double maxLon, double maxLat);

    HistoricalDataDTO.Response update(Long id, HistoricalDataDTO.UpdateRequest request);

    void delete(Long id);
}
