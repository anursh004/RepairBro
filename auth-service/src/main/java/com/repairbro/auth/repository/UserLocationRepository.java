package com.repairbro.auth.repository;

import com.repairbro.auth.model.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, UUID> {

    List<UserLocation> findByUserId(UUID userId);

    List<UserLocation> findByBranchId(UUID branchId);

    Optional<UserLocation> findByUserIdAndBranchId(UUID userId, UUID branchId);

    void deleteByUserIdAndBranchId(UUID userId, UUID branchId);
}
