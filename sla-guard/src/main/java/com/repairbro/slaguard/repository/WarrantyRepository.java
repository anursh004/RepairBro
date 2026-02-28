package com.repairbro.slaguard.repository;

import com.repairbro.slaguard.model.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, UUID> {

    Optional<Warranty> findByTicketId(UUID ticketId);

    List<Warranty> findByCustomerId(UUID customerId);

    List<Warranty> findByBranchId(UUID branchId);

    @Query("SELECT w FROM Warranty w WHERE w.status = 'ACTIVE' AND w.expiryDate < :now")
    List<Warranty> findExpiredWarranties(Instant now);

    List<Warranty> findByCustomerIdAndStatus(UUID customerId, Warranty.WarrantyStatus status);
}
