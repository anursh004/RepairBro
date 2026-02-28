package com.repairbro.repaircore.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.repaircore.dto.BranchDTO;
import com.repairbro.repaircore.dto.CreateBranchRequest;
import com.repairbro.repaircore.service.BranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_BRANCH_CREATE')")
    public ResponseEntity<ApiResponse<BranchDTO>> create(@Valid @RequestBody CreateBranchRequest request) {
        BranchDTO dto = branchService.createBranch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(dto, "Branch created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_BRANCH_VIEW')")
    public ResponseEntity<ApiResponse<BranchDTO>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(branchService.getBranch(id)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_BRANCH_VIEW')")
    public ResponseEntity<ApiResponse<List<BranchDTO>>> getActive() {
        return ResponseEntity.ok(ApiResponse.ok(branchService.getActiveBranches()));
    }

    @GetMapping("/city/{city}")
    @PreAuthorize("hasAuthority('PERM_BRANCH_VIEW')")
    public ResponseEntity<ApiResponse<List<BranchDTO>>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(ApiResponse.ok(branchService.getByCity(city)));
    }

    @GetMapping("/tier/{tier}")
    @PreAuthorize("hasAuthority('PERM_BRANCH_VIEW')")
    public ResponseEntity<ApiResponse<List<BranchDTO>>> getByTier(@PathVariable int tier) {
        return ResponseEntity.ok(ApiResponse.ok(branchService.getByTier(tier)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_BRANCH_EDIT')")
    public ResponseEntity<ApiResponse<BranchDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateBranchRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(branchService.updateBranch(id, request), "Branch updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_BRANCH_DEACTIVATE')")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        branchService.deactivateBranch(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Branch deactivated"));
    }
}
