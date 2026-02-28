package com.repairbro.fixbill.repository;

import com.repairbro.fixbill.model.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, UUID> {

    Optional<Estimate> findByTicketId(UUID ticketId);

    List<Estimate> findByBranchId(UUID branchId);

    List<Estimate> findByCustomerId(UUID customerId);

    List<Estimate> findByStatus(Estimate.EstimateStatus status);
}
