package com.repairbro.diagflow.repository;

import com.repairbro.diagflow.model.DiagFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DiagFlowRepository extends JpaRepository<DiagFlow, UUID> {
    List<DiagFlow> findByDeviceTypeAndActiveTrue(String deviceType);

    List<DiagFlow> findBySymptomCategoryAndActiveTrue(String symptomCategory);

    List<DiagFlow> findByActiveTrue();
}
