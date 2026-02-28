package com.repairbro.partsflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchInventoryId implements Serializable {
    private UUID branchId;
    private UUID sparePartId;
}
