package com.repairbro.insightengine.repository;

import com.repairbro.insightengine.model.BranchKpiDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BranchKpiRepository extends JpaRepository<BranchKpiDaily, UUID> {
    List<BranchKpiDaily> findByBranchIdAndDateBetween(UUID branchId, LocalDate from, LocalDate to);

    List<BranchKpiDaily> findByDate(LocalDate date);
}
