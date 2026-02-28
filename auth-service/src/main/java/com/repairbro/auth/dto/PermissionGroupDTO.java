package com.repairbro.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionGroupDTO {
    private UUID id;
    private String name;
    private String description;
    private String level;
    private boolean systemDefined;
    private boolean active;
    private List<PermissionDTO> permissions;
    private int userCount;
}
