package com.repairbro.diagflow.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.diagflow.model.DiagFlow;
import com.repairbro.diagflow.model.DiagnosisEvaluation;
import com.repairbro.diagflow.service.DiagFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/diagnostics")
@RequiredArgsConstructor
public class DiagFlowController {

    private final DiagFlowService diagFlowService;

    @GetMapping("/library")
    @PreAuthorize("hasAuthority('PERM_DIAG_VIEW_LIBRARY')")
    public ResponseEntity<ApiResponse<List<DiagFlow>>> getLibrary() {
        return ResponseEntity.ok(ApiResponse.ok(diagFlowService.getLibrary()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_DIAG_VIEW_LIBRARY')")
    public ResponseEntity<ApiResponse<DiagFlow>> getFlow(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(diagFlowService.getFlow(id)));
    }

    @GetMapping("/device/{deviceType}")
    @PreAuthorize("hasAuthority('PERM_DIAG_VIEW_LIBRARY')")
    public ResponseEntity<ApiResponse<List<DiagFlow>>> getByDevice(@PathVariable String deviceType) {
        return ResponseEntity.ok(ApiResponse.ok(diagFlowService.getFlowsByDevice(deviceType)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PERM_DIAG_CREATE_FLOW')")
    public ResponseEntity<ApiResponse<DiagFlow>> createFlow(@RequestBody DiagFlow flow) {
        DiagFlow created = diagFlowService.createFlow(flow);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Diagnostic flow created"));
    }

    @PostMapping("/evaluate")
    @PreAuthorize("hasAuthority('PERM_DIAG_EVALUATE')")
    public ResponseEntity<ApiResponse<DiagnosisEvaluation>> evaluate(@RequestBody Map<String, String> request) {
        UUID ticketId = UUID.fromString(request.get("ticketId"));
        String deviceType = request.get("deviceType");
        String symptom = request.get("symptom");
        DiagnosisEvaluation eval = diagFlowService.evaluate(ticketId, deviceType, symptom);
        return ResponseEntity.ok(ApiResponse.ok(eval, "Diagnostic evaluation started"));
    }
}
