package com.repairbro.repairsim.repository;

import com.repairbro.repairsim.model.SimulationResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SimulationResultRepository extends JpaRepository<SimulationResultEntity, UUID> {

    List<SimulationResultEntity> findByScenarioId(UUID scenarioId);
}
