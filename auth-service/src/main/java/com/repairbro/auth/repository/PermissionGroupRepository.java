package com.repairbro.auth.repository;

import com.repairbro.auth.model.PermissionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, UUID> {

    Optional<PermissionGroup> findByName(String name);

    List<PermissionGroup> findByActiveTrue();

    List<PermissionGroup> findByLevel(String level);

    boolean existsByName(String name);
}
