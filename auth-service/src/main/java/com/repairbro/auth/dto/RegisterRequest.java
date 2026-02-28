package com.repairbro.auth.dto;

import com.repairbro.auth.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @NotBlank
    @Size(max = 128)
    private String fullName;

    private String phone;

    /** Optional — null for HQ/admin users */
    private UUID branchId;

    @NotEmpty
    private Set<Role> roles;
}
