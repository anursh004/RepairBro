package com.repairbro.fixbill.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.fixbill.model.Estimate;
import com.repairbro.fixbill.service.EstimateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/estimates")
@RequiredArgsConstructor
public class EstimateController {

    private final EstimateService estimateService;

    @Data
    public static class CreateEstimateRequest {
        private UUID ticketId;
        private UUID branchId;
        private UUID customerId;
        private BigDecimal laborCost;
        private BigDecimal partsCost;
        private int estimatedDays = 2;
        private String workDescription;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_ESTIMATE_CREATE')")
    public ResponseEntity<ApiResponse<Estimate>> create(@RequestBody CreateEstimateRequest req) {
        Estimate estimate = estimateService.createEstimate(
                req.getTicketId(), req.getBranchId(), req.getCustomerId(),
                req.getLaborCost(), req.getPartsCost(),
                req.getEstimatedDays(), req.getWorkDescription());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(estimate, "Estimate created"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_ESTIMATE_VIEW')")
    public ResponseEntity<ApiResponse<Estimate>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(estimateService.getEstimate(id)));
    }

    @GetMapping("/ticket/{ticketId}")
    @PreAuthorize("hasAuthority('PERM_ESTIMATE_VIEW')")
    public ResponseEntity<ApiResponse<Estimate>> getByTicket(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(ApiResponse.ok(estimateService.getByTicket(ticketId)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('PERM_ESTIMATE_APPROVE')")
    public ResponseEntity<ApiResponse<Estimate>> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(estimateService.approveEstimate(id), "Estimate approved"));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('PERM_ESTIMATE_REJECT')")
    public ResponseEntity<ApiResponse<Estimate>> reject(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(estimateService.rejectEstimate(id), "Estimate rejected"));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAuthority('PERM_ESTIMATE_VIEW')")
    public ResponseEntity<ApiResponse<List<Estimate>>> getByBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(ApiResponse.ok(estimateService.getByBranch(branchId)));
    }
}
