package com.repairbro.auth.controller;

import com.repairbro.auth.dto.CreateTechnicianRequest;
import com.repairbro.auth.dto.TechnicianDTO;
import com.repairbro.auth.service.TechnicianService;
import com.repairbro.commons.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/technicians")
@RequiredArgsConstructor
public class TechnicianController {

    private final TechnicianService techService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_TECHNICIAN_CREATE')")
    public ResponseEntity<ApiResponse<TechnicianDTO>> create(@Valid @RequestBody CreateTechnicianRequest request) {
        TechnicianDTO dto = techService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(dto, "Technician profile created"));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('PERM_TECHNICIAN_VIEW')")
    public ResponseEntity<ApiResponse<TechnicianDTO>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(techService.getByUserId(userId)));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAuthority('PERM_TECHNICIAN_VIEW')")
    public ResponseEntity<ApiResponse<List<TechnicianDTO>>> getByBranch(@PathVariable UUID branchId) {
        return ResponseEntity.ok(ApiResponse.ok(techService.getByBranch(branchId)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERM_TECHNICIAN_VIEW')")
    public ResponseEntity<ApiResponse<List<TechnicianDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(techService.getAll()));
    }

    @PutMapping("/{profileId}")
    @PreAuthorize("hasAuthority('PERM_TECHNICIAN_EDIT')")
    public ResponseEntity<ApiResponse<TechnicianDTO>> update(
            @PathVariable UUID profileId,
            @Valid @RequestBody CreateTechnicianRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(techService.updateProfile(profileId, request), "Profile updated"));
    }
}
