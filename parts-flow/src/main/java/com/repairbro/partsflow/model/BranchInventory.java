package com.repairbro.partsflow.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "branch_inventory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@IdClass(BranchInventoryId.class)
public class BranchInventory {

    @Id
    @EqualsAndHashCode.Include
    private UUID branchId;

    @Id
    @EqualsAndHashCode.Include
    private UUID sparePartId;

    @Builder.Default
    private int qty = 0;

    @Builder.Default
    private int minStock = 5;

    @UpdateTimestamp
    private Instant updatedAt;
}
