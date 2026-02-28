package com.repairbro.franchisehub.repository;

import com.repairbro.franchisehub.model.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, UUID> {
    Optional<Franchise> findByBranchId(UUID branchId);

    List<Franchise> findByStatus(Franchise.FranchiseStatus status);
}
