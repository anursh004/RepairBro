package com.repairbro.repairsim.service;

import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.repairsim.engine.DiscreteEventEngine;
import com.repairbro.repairsim.model.SimulationResultEntity;
import com.repairbro.repairsim.model.SimulationScenario;
import com.repairbro.repairsim.repository.ScenarioRepository;
import com.repairbro.repairsim.repository.SimulationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationService {

    private final ScenarioRepository scenarioRepo;
    private final SimulationResultRepository resultRepo;

    @Transactional
    public SimulationScenario createScenario(SimulationScenario scenario) {
        scenario.setStatus(SimulationScenario.ScenarioStatus.CREATED);
        return scenarioRepo.save(scenario);
    }

    @Transactional(readOnly = true)
    public SimulationScenario getScenario(UUID id) {
        return scenarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SimulationScenario", id.toString()));
    }

    @Transactional(readOnly = true)
    public List<SimulationScenario> getAllScenarios() {
        return scenarioRepo.findAll();
    }

    /**
     * Run a simulation: creates a DiscreteEventEngine for each branch,
     * runs the simulation, and persists results.
     */
    @Transactional
    public List<SimulationResultEntity> runSimulation(UUID scenarioId) {
        SimulationScenario scenario = getScenario(scenarioId);
        scenario.setStatus(SimulationScenario.ScenarioStatus.RUNNING);
        scenarioRepo.save(scenario);

        log.info("Starting simulation: {} — {} branches, {} days, {} tickets/day/branch",
                scenario.getName(), scenario.getBranchCount(),
                scenario.getSimulationDays(), scenario.getAvgDemandsPerDay());

        List<SimulationResultEntity> results = new ArrayList<>();

        try {
            for (int i = 0; i < scenario.getBranchCount(); i++) {
                DiscreteEventEngine engine = new DiscreteEventEngine(scenario);
                SimulationResultEntity result = engine.runForBranch(i);
                result.setScenario(scenario);
                results.add(result);

                log.info("Branch {} complete: revenue=₹{}, profit=₹{}, margin={}%, FTFR={}, MTTR={}h",
                        i, (long) result.getTotalRevenue(), (long) result.getNetProfit(),
                        result.getProfitMarginPercent(), result.getFirstTimeFixRate(),
                        result.getMeanRepairTimeHours());
            }

            resultRepo.saveAll(results);
            scenario.setStatus(SimulationScenario.ScenarioStatus.COMPLETED);
            scenarioRepo.save(scenario);

            log.info("Simulation complete: {} branch results persisted", results.size());
        } catch (Exception e) {
            scenario.setStatus(SimulationScenario.ScenarioStatus.FAILED);
            scenarioRepo.save(scenario);
            log.error("Simulation failed: {}", e.getMessage(), e);
            throw e;
        }

        return results;
    }

    @Transactional(readOnly = true)
    public List<SimulationResultEntity> getResults(UUID scenarioId) {
        return resultRepo.findByScenarioId(scenarioId);
    }
}
