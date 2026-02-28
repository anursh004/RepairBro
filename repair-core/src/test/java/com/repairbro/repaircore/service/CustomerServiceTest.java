package com.repairbro.repaircore.service;

import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.repaircore.dto.CreateCustomerRequest;
import com.repairbro.repaircore.dto.CustomerDTO;
import com.repairbro.repaircore.model.Customer;
import com.repairbro.repaircore.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepo;
    @InjectMocks
    private CustomerService customerService;

    private CreateCustomerRequest validRequest;
    private Customer savedCustomer;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        validRequest = new CreateCustomerRequest();
        validRequest.setName("Rajesh Sharma");
        validRequest.setPhone("9876543210");
        validRequest.setEmail("rajesh@gmail.com");
        validRequest.setAddress("301 Sunrise Apts");
        validRequest.setCity("Mumbai");
        validRequest.setPincode("400069");
        validRequest.setPreferredChannel("SMS");

        savedCustomer = Customer.builder()
                .id(customerId)
                .name("Rajesh Sharma")
                .phone("9876543210")
                .email("rajesh@gmail.com")
                .address("301 Sunrise Apts")
                .city("Mumbai")
                .pincode("400069")
                .preferredChannel("SMS")
                .totalRepairs(0)
                .build();
    }

    @Nested
    @DisplayName("createCustomer()")
    class CreateCustomer {

        @Test
        @DisplayName("should create customer with valid request")
        void shouldCreateCustomer() {
            when(customerRepo.existsByPhone("9876543210")).thenReturn(false);
            when(customerRepo.save(any(Customer.class))).thenReturn(savedCustomer);

            CustomerDTO result = customerService.createCustomer(validRequest);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Rajesh Sharma");
            assertThat(result.getPhone()).isEqualTo("9876543210");
            assertThat(result.getCity()).isEqualTo("Mumbai");
            assertThat(result.getPreferredChannel()).isEqualTo("SMS");
            verify(customerRepo).save(any(Customer.class));
        }

        @Test
        @DisplayName("should throw exception for duplicate phone")
        void shouldRejectDuplicatePhone() {
            when(customerRepo.existsByPhone("9876543210")).thenReturn(true);

            assertThatThrownBy(() -> customerService.createCustomer(validRequest))
                    .isInstanceOf(RepairBroException.class)
                    .hasMessageContaining("already exists");

            verify(customerRepo, never()).save(any());
        }

        @Test
        @DisplayName("should default preferredChannel to SMS when null")
        void shouldDefaultChannelToSMS() {
            validRequest.setPreferredChannel(null);
            when(customerRepo.existsByPhone(anyString())).thenReturn(false);
            when(customerRepo.save(any(Customer.class))).thenAnswer(inv -> {
                Customer c = inv.getArgument(0);
                c.setId(customerId);
                return c;
            });

            CustomerDTO result = customerService.createCustomer(validRequest);
            assertThat(result.getPreferredChannel()).isEqualTo("SMS");
        }
    }

    @Nested
    @DisplayName("getCustomer()")
    class GetCustomer {

        @Test
        @DisplayName("should return customer by ID")
        void shouldReturnCustomer() {
            when(customerRepo.findById(customerId)).thenReturn(Optional.of(savedCustomer));

            CustomerDTO result = customerService.getCustomer(customerId);

            assertThat(result.getId()).isEqualTo(customerId);
            assertThat(result.getName()).isEqualTo("Rajesh Sharma");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException for missing ID")
        void shouldThrowNotFound() {
            UUID missingId = UUID.randomUUID();
            when(customerRepo.findById(missingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.getCustomer(missingId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByPhone()")
    class GetByPhone {

        @Test
        @DisplayName("should return customer by phone")
        void shouldReturnByPhone() {
            when(customerRepo.findByPhone("9876543210")).thenReturn(Optional.of(savedCustomer));

            CustomerDTO result = customerService.getByPhone("9876543210");
            assertThat(result.getPhone()).isEqualTo("9876543210");
        }

        @Test
        @DisplayName("should throw for unknown phone")
        void shouldThrowForUnknownPhone() {
            when(customerRepo.findByPhone("0000000000")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customerService.getByPhone("0000000000"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateCustomer()")
    class UpdateCustomer {

        @Test
        @DisplayName("should update customer fields")
        void shouldUpdate() {
            when(customerRepo.findById(customerId)).thenReturn(Optional.of(savedCustomer));
            when(customerRepo.save(any(Customer.class))).thenReturn(savedCustomer);

            validRequest.setName("Rajesh Kumar");
            validRequest.setCity("Pune");

            CustomerDTO result = customerService.updateCustomer(customerId, validRequest);
            assertThat(result).isNotNull();
            verify(customerRepo).save(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("incrementRepairCount()")
    class IncrementRepairs {

        @Test
        @DisplayName("should increment totalRepairs by 1")
        void shouldIncrement() {
            savedCustomer.setTotalRepairs(3);
            when(customerRepo.findById(customerId)).thenReturn(Optional.of(savedCustomer));
            when(customerRepo.save(any(Customer.class))).thenReturn(savedCustomer);

            customerService.incrementRepairCount(customerId);

            assertThat(savedCustomer.getTotalRepairs()).isEqualTo(4);
            verify(customerRepo).save(savedCustomer);
        }
    }

    @Nested
    @DisplayName("searchByName()")
    class SearchByName {

        @Test
        @DisplayName("should return matching customers")
        void shouldSearch() {
            when(customerRepo.findByNameContainingIgnoreCase("Raj"))
                    .thenReturn(List.of(savedCustomer));

            List<CustomerDTO> results = customerService.searchByName("Raj");
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getName()).contains("Rajesh");
        }

        @Test
        @DisplayName("should return empty list for no matches")
        void shouldReturnEmpty() {
            when(customerRepo.findByNameContainingIgnoreCase("NonExistent"))
                    .thenReturn(List.of());

            assertThat(customerService.searchByName("NonExistent")).isEmpty();
        }
    }
}
