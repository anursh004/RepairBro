package com.repairbro.slaguard.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.slaguard.model.*;
import com.repairbro.slaguard.service.SlaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SlaController {

    private final SlaService slaService;

    @GetMapping("/sla/branch/{branchId}")
    @PreAuthorize("hasAuthority('PERM_SLA_VIEW')")
    public ResponseEntity<ApiResponse<List<SlaRecord>>> getSlas(@PathVariable UUID branchId) {
        return ResponseEntity.ok(ApiResponse.ok(slaService.getByBranch(branchId)));
    }

    @PostMapping("/sla")
    @PreAuthorize("hasAuthority('PERM_SLA_CREATE')")
    public ResponseEntity<ApiResponse<SlaRecord>> createSla(@RequestBody Map<String, String> req) {
        SlaRecord sla = slaService.createSla(
                UUID.fromString(req.get("ticketId")),
                UUID.fromString(req.get("branchId")),
                req.get("slaType"),
                Instant.parse(req.get("deadline")));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(sla));
    }

    @GetMapping("/complaints/branch/{branchId}")
    @PreAuthorize("hasAuthority('PERM_COMPLAINT_VIEW')")
    public ResponseEntity<ApiResponse<List<Complaint>>> getComplaints(@PathVariable UUID branchId) {
        return ResponseEntity.ok(ApiResponse.ok(slaService.getComplaints(branchId)));
    }

    @PostMapping("/complaints")
    @PreAuthorize("hasAuthority('PERM_COMPLAINT_FILE')")
    public ResponseEntity<ApiResponse<Complaint>> fileComplaint(@RequestBody Complaint complaint) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(slaService.fileComplaint(complaint)));
    }
}
