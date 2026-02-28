package com.repairbro.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

    /** Groups the user belongs to */
    private List<String> groups;

    /** Effective permissions (union of all groups) */
    private List<String> permissions;

    /** Location (branch) IDs the user is mapped to */
    private List<String> locations;
}
