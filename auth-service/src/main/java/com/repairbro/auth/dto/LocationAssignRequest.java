package com.repairbro.auth.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class LocationAssignRequest {
    private UUID branchId;
    private boolean primaryLocation;
}
