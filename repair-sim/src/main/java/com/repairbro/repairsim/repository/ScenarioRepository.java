package com.repairbro.repairsim.repository;

import com.repairbro.repairsim.model.SimulationScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScenarioRepository extends JpaRepository<SimulationScenario, UUID> {
}
