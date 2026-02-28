package com.repairbro.auth.dto;

import com.repairbro.auth.model.Role;
import com.repairbro.auth.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private UUID branchId;

    /** @deprecated Legacy roles, kept for backwards compatibility */
    @Deprecated
    private Set<Role> roles;

    /** Group names the user belongs to */
    private List<String> groups;

    /** Effective permissions (union of all groups) */
    private List<String> permissions;

    /** Locations the user is mapped to */
    private List<LocationDTO> locations;

    private UserStatus status;
    private Instant createdAt;
}
