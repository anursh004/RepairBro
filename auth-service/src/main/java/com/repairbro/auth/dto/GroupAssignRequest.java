package com.repairbro.auth.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class GroupAssignRequest {
    private UUID groupId;
}
