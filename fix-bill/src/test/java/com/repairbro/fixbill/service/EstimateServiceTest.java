package com.repairbro.fixbill.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.fixbill.model.Estimate;
import com.repairbro.fixbill.repository.EstimateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EstimateService Unit Tests")
class EstimateServiceTest {

    @Mock
    private EstimateRepository estimateRepo;
    @Mock
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;
    @InjectMocks
    private EstimateService estimateService;

    private UUID ticketId, branchId, customerId, estimateId;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        branchId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        estimateId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("createEstimate()")
    class CreateEstimate {

        @Test
        @DisplayName("should calculate GST at 18% correctly")
        void shouldCalculateGST() {
            BigDecimal labor = new BigDecimal("1000");
            BigDecimal parts = new BigDecimal("2000");

            when(estimateRepo.save(any(Estimate.class))).thenAnswer(inv -> {
                Estimate e = inv.getArgument(0);
                e.setId(estimateId);
                return e;
            });

            Estimate result = estimateService.createEstimate(
                    ticketId, branchId, customerId, labor, parts, 2, "Screen replacement");

            // Subtotal = 1000 + 2000 = 3000, Tax = 3000 * 18% = 540, Total = 3540
            assertThat(result.getTaxRate()).isEqualByComparingTo("18.00");
            assertThat(result.getTaxAmount()).isEqualByComparingTo("540.00");
            assertThat(result.getTotalEstimate()).isEqualByComparingTo("3540.00");
            assertThat(result.getLaborCost()).isEqualByComparingTo("1000");
            assertThat(result.getPartsCost()).isEqualByComparingTo("2000");
        }

        @Test
        @DisplayName("should set status to PENDING by default")
        void shouldDefaultToPending() {
            when(estimateRepo.save(any(Estimate.class))).thenAnswer(inv -> inv.getArgument(0));

            Estimate result = estimateService.createEstimate(
                    ticketId, branchId, customerId,
                    new BigDecimal("500"), new BigDecimal("1500"), 1, "Battery swap");

            assertThat(result.getStatus()).isEqualTo(Estimate.EstimateStatus.PENDING);
        }

        @Test
        @DisplayName("should publish EstimateCreated event")
        void shouldPublishEvent() {
            when(estimateRepo.save(any(Estimate.class))).thenAnswer(inv -> {
                Estimate e = inv.getArgument(0);
                e.setId(estimateId);
                return e;
            });

            estimateService.createEstimate(ticketId, branchId, customerId,
                    new BigDecimal("1000"), new BigDecimal("1000"), 1, "Test");

            verify(kafkaTemplate).send(anyString(), anyString(), any(DomainEvent.class));
        }
    }

    @Nested
    @DisplayName("approveEstimate()")
    class ApproveEstimate {

        @Test
        @DisplayName("should approve PENDING estimate")
        void shouldApprove() {
            Estimate pending = Estimate.builder()
                    .id(estimateId).ticketId(ticketId)
                    .status(Estimate.EstimateStatus.PENDING)
                    .build();

            when(estimateRepo.findById(estimateId)).thenReturn(Optional.of(pending));
            when(estimateRepo.save(any(Estimate.class))).thenAnswer(inv -> inv.getArgument(0));

            Estimate result = estimateService.approveEstimate(estimateId);

            assertThat(result.getStatus()).isEqualTo(Estimate.EstimateStatus.APPROVED);
            assertThat(result.getApprovedAt()).isNotNull();
        }

        @Test
        @DisplayName("should reject approval of already-approved estimate")
        void shouldRejectDoubleApproval() {
            Estimate approved = Estimate.builder()
                    .id(estimateId).ticketId(ticketId)
                    .status(Estimate.EstimateStatus.APPROVED)
                    .build();

            when(estimateRepo.findById(estimateId)).thenReturn(Optional.of(approved));

            assertThatThrownBy(() -> estimateService.approveEstimate(estimateId))
                    .isInstanceOf(RepairBroException.class)
                    .hasMessageContaining("not in PENDING");
        }
    }

    @Nested
    @DisplayName("rejectEstimate()")
    class RejectEstimate {

        @Test
        @DisplayName("should set status to REJECTED")
        void shouldReject() {
            Estimate pending = Estimate.builder()
                    .id(estimateId).ticketId(ticketId)
                    .status(Estimate.EstimateStatus.PENDING)
                    .build();

            when(estimateRepo.findById(estimateId)).thenReturn(Optional.of(pending));
            when(estimateRepo.save(any(Estimate.class))).thenAnswer(inv -> inv.getArgument(0));

            Estimate result = estimateService.rejectEstimate(estimateId);
            assertThat(result.getStatus()).isEqualTo(Estimate.EstimateStatus.REJECTED);
        }
    }

    @Nested
    @DisplayName("getEstimate()")
    class GetEstimate {

        @Test
        @DisplayName("should throw ResourceNotFoundException for missing estimate")
        void shouldThrowNotFound() {
            when(estimateRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> estimateService.getEstimate(UUID.randomUUID()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
