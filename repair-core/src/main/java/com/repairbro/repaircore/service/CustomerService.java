package com.repairbro.repaircore.service;

import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.repaircore.dto.CreateCustomerRequest;
import com.repairbro.repaircore.dto.CustomerDTO;
import com.repairbro.repaircore.model.Customer;
import com.repairbro.repaircore.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepo;

    @Transactional
    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        if (customerRepo.existsByPhone(request.getPhone())) {
            throw new RepairBroException(
                    "Customer with phone " + request.getPhone() + " already exists",
                    HttpStatus.CONFLICT, "DUPLICATE_PHONE");
        }

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .pincode(request.getPincode())
                .preferredChannel(request.getPreferredChannel() != null
                        ? request.getPreferredChannel()
                        : "SMS")
                .build();

        customer = customerRepo.save(customer);
        log.info("Customer created: {} [{}]", customer.getName(), customer.getId());
        return toDTO(customer);
    }

    @Transactional(readOnly = true)
    public CustomerDTO getCustomer(UUID id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public CustomerDTO getByPhone(String phone) {
        return toDTO(customerRepo.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "phone=" + phone)));
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> searchByName(String name) {
        return customerRepo.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<CustomerDTO> getAll() {
        return customerRepo.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional
    public CustomerDTO updateCustomer(UUID id, CreateCustomerRequest request) {
        Customer customer = findOrThrow(id);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setPincode(request.getPincode());
        if (request.getPreferredChannel() != null) {
            customer.setPreferredChannel(request.getPreferredChannel());
        }
        customer = customerRepo.save(customer);
        return toDTO(customer);
    }

    @Transactional
    public void incrementRepairCount(UUID customerId) {
        Customer c = findOrThrow(customerId);
        c.setTotalRepairs(c.getTotalRepairs() + 1);
        customerRepo.save(c);
    }

    private Customer findOrThrow(UUID id) {
        return customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id.toString()));
    }

    private CustomerDTO toDTO(Customer c) {
        return CustomerDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .phone(c.getPhone())
                .email(c.getEmail())
                .address(c.getAddress())
                .city(c.getCity())
                .pincode(c.getPincode())
                .preferredChannel(c.getPreferredChannel())
                .totalRepairs(c.getTotalRepairs())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
