package com.repairbro.franchisehub.repository;

import com.repairbro.franchisehub.model.RoyaltyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoyaltyRepository extends JpaRepository<RoyaltyRecord, UUID> {
    List<RoyaltyRecord> findByFranchiseId(UUID franchiseId);

    List<RoyaltyRecord> findByFranchiseIdAndPeriod(UUID franchiseId, String period);
}
