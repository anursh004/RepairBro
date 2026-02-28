package com.repairbro.franchisehub.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.franchisehub.model.*;
import com.repairbro.franchisehub.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;

    @GetMapping("/franchises")
    @PreAuthorize("hasAuthority('PERM_FRANCHISE_VIEW')")
    public ResponseEntity<ApiResponse<List<Franchise>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(franchiseService.getAll()));
    }

    @GetMapping("/franchises/{id}")
    @PreAuthorize("hasAuthority('PERM_FRANCHISE_VIEW')")
    public ResponseEntity<ApiResponse<Franchise>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(franchiseService.get(id)));
    }

    @PostMapping("/franchises")
    @PreAuthorize("hasAuthority('PERM_FRANCHISE_ONBOARD')")
    public ResponseEntity<ApiResponse<Franchise>> onboard(@RequestBody Franchise franchise) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(franchiseService.onboard(franchise)));
    }

    @GetMapping("/royalties/{franchiseId}")
    @PreAuthorize("hasAuthority('PERM_ROYALTY_VIEW')")
    public ResponseEntity<ApiResponse<List<RoyaltyRecord>>> getRoyalties(@PathVariable UUID franchiseId) {
        return ResponseEntity.ok(ApiResponse.ok(franchiseService.getRoyalties(franchiseId)));
    }

    @PostMapping("/royalties/calculate")
    @PreAuthorize("hasAuthority('PERM_ROYALTY_CALCULATE')")
    public ResponseEntity<ApiResponse<RoyaltyRecord>> calcRoyalty(@RequestBody Map<String, String> req) {
        UUID fId = UUID.fromString(req.get("franchiseId"));
        String period = req.get("period");
        BigDecimal revenue = new BigDecimal(req.get("grossRevenue"));
        return ResponseEntity.ok(ApiResponse.ok(franchiseService.calculateRoyalty(fId, period, revenue)));
    }
}
