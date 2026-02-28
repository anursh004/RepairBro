package com.repairbro.slaguard.repository;

import com.repairbro.slaguard.model.SlaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface SlaRecordRepository extends JpaRepository<SlaRecord, UUID> {
    List<SlaRecord> findByBranchId(UUID branchId);

    List<SlaRecord> findByTicketId(UUID ticketId);

    @Query("SELECT s FROM SlaRecord s WHERE s.status = 'ACTIVE' AND s.deadline < :now")
    List<SlaRecord> findBreachedSlas(Instant now);
}
