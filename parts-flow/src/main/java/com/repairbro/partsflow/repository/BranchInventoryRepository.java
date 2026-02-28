package com.repairbro.partsflow.repository;

import com.repairbro.partsflow.model.BranchInventory;
import com.repairbro.partsflow.model.BranchInventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BranchInventoryRepository extends JpaRepository<BranchInventory, BranchInventoryId> {

    List<BranchInventory> findByBranchId(UUID branchId);

    @Query("SELECT bi FROM BranchInventory bi WHERE bi.branchId = :branchId AND bi.qty <= bi.minStock")
    List<BranchInventory> findLowStock(@Param("branchId") UUID branchId);
}
