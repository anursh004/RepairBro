package com.repairbro.repairsim.controller;

import com.repairbro.commons.dto.ApiResponse;
import com.repairbro.repairsim.model.SimulationResultEntity;
import com.repairbro.repairsim.model.SimulationScenario;
import com.repairbro.repairsim.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/simulation")
@RequiredArgsConstructor
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping("/scenarios")
    @PreAuthorize("hasAuthority('PERM_SIMULATION_CREATE')")
    public ResponseEntity<ApiResponse<SimulationScenario>> createScenario(
            @RequestBody SimulationScenario scenario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(simulationService.createScenario(scenario), "Scenario created"));
    }

    @GetMapping("/scenarios")
    @PreAuthorize("hasAuthority('PERM_SIMULATION_VIEW')")
    public ResponseEntity<ApiResponse<List<SimulationScenario>>> listScenarios() {
        return ResponseEntity.ok(ApiResponse.ok(simulationService.getAllScenarios()));
    }

    @GetMapping("/scenarios/{id}")
    @PreAuthorize("hasAuthority('PERM_SIMULATION_VIEW')")
    public ResponseEntity<ApiResponse<SimulationScenario>> getScenario(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(simulationService.getScenario(id)));
    }

    @PostMapping("/scenarios/{id}/run")
    @PreAuthorize("hasAuthority('PERM_SIMULATION_RUN')")
    public ResponseEntity<ApiResponse<List<SimulationResultEntity>>> run(@PathVariable UUID id) {
        List<SimulationResultEntity> results = simulationService.runSimulation(id);
        return ResponseEntity.ok(ApiResponse.ok(results, "Simulation completed"));
    }

    @GetMapping("/scenarios/{id}/results")
    @PreAuthorize("hasAuthority('PERM_SIMULATION_VIEW')")
    public ResponseEntity<ApiResponse<List<SimulationResultEntity>>> getResults(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(simulationService.getResults(id)));
    }
}
