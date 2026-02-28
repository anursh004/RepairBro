package com.repairbro.insightengine.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.insightengine.model.BranchKpiDaily;
import com.repairbro.insightengine.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/metrics/branch/{branchId}")
    @PreAuthorize("hasAuthority('PERM_METRICS_VIEW')")
    public ResponseEntity<ApiResponse<List<BranchKpiDaily>>> getBranchMetrics(
            @PathVariable UUID branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponse.ok(metricsService.getBranchMetrics(branchId, from, to)));
    }

    @GetMapping("/reports/daily")
    @PreAuthorize("hasAuthority('PERM_REPORT_VIEW')")
    public ResponseEntity<ApiResponse<List<BranchKpiDaily>>> getDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.ok(metricsService.getDailyReport(date)));
    }
}
