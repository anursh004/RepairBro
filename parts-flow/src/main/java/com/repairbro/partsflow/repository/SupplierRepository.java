package com.repairbro.partsflow.repository;

import com.repairbro.partsflow.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    List<Supplier> findByActiveTrue();

    List<Supplier> findByNameContainingIgnoreCase(String name);
}
