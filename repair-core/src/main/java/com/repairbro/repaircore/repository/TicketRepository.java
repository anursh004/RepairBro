package com.repairbro.repaircore.repository;

import com.repairbro.repaircore.model.RepairTicket;
import com.repairbro.repaircore.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<RepairTicket, UUID> {

    Page<RepairTicket> findByBranchId(UUID branchId, Pageable pageable);

    Page<RepairTicket> findByBranchIdAndStatus(UUID branchId, TicketStatus status, Pageable pageable);

    Page<RepairTicket> findByCustomerId(UUID customerId, Pageable pageable);

    Page<RepairTicket> findByAssignedTechId(UUID techId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM RepairTicket t WHERE t.branchId = :branchId AND t.status NOT IN ('COMPLETED', 'CANCELLED')")
    long countActiveByBranch(@Param("branchId") UUID branchId);
}
