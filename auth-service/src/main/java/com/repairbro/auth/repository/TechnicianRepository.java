package com.repairbro.auth.repository;

import com.repairbro.auth.model.SkillLevel;
import com.repairbro.auth.model.TechnicianProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TechnicianRepository extends JpaRepository<TechnicianProfile, UUID> {

    Optional<TechnicianProfile> findByUserId(UUID userId);

    List<TechnicianProfile> findByBranchIdAndActiveTrue(UUID branchId);

    List<TechnicianProfile> findBySkillLevel(SkillLevel skillLevel);

    List<TechnicianProfile> findByBranchId(UUID branchId);

    long countByBranchIdAndActiveTrue(UUID branchId);
}
