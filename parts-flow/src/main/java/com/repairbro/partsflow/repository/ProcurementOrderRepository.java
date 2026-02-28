package com.repairbro.partsflow.repository;

import com.repairbro.partsflow.model.ProcurementOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProcurementOrderRepository extends JpaRepository<ProcurementOrder, UUID> {
    Page<ProcurementOrder> findByBranchId(UUID branchId, Pageable pageable);
}
