package com.repairbro.repaircore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateBranchRequest {

    @NotBlank(message = "Branch name is required")
    @Size(max = 128)
    private String name;

    @NotBlank(message = "City is required")
    @Size(max = 64)
    private String city;

    /** City tier: 1 = Metro, 2 = Tier-2, 3 = Tier-3 */
    private int tier = 2;

    private String address;

    @Size(max = 20)
    private String phone;

    @Size(max = 128)
    private String email;

    private BigDecimal monthlyRent;
}
