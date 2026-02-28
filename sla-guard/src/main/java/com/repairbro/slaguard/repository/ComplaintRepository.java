package com.repairbro.slaguard.repository;

import com.repairbro.slaguard.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, UUID> {
    List<Complaint> findByBranchId(UUID branchId);

    List<Complaint> findByTicketId(UUID ticketId);
}
