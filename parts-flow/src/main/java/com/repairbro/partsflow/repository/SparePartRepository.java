package com.repairbro.partsflow.repository;

import com.repairbro.partsflow.model.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, UUID> {
    Optional<SparePart> findBySku(String sku);
}
