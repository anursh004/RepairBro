package com.repairbro.repaircore.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class CustomerDTO {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String pincode;
    private String preferredChannel;
    private int totalRepairs;
    private Instant createdAt;
}
