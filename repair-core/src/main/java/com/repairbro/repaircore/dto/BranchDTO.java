package com.repairbro.repaircore.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class BranchDTO {
    private UUID id;
    private String name;
    private String city;
    private int tier;
    private String address;
    private String phone;
    private String email;
    private BigDecimal monthlyRent;
    private boolean active;
    private Instant createdAt;
}
