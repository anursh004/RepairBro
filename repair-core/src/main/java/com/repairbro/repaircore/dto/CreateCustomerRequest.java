package com.repairbro.repaircore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCustomerRequest {

    @NotBlank(message = "Customer name is required")
    @Size(max = 128)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String phone;

    @Size(max = 128)
    private String email;

    private String address;

    @Size(max = 64)
    private String city;

    @Size(max = 10)
    private String pincode;

    private String preferredChannel;
}
