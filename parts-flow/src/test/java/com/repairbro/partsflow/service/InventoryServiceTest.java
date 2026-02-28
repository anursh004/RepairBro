package com.repairbro.partsflow.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.partsflow.model.*;
import com.repairbro.partsflow.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService Unit Tests")
class InventoryServiceTest {

    @Mock
    private SparePartRepository sparePartRepo;
    @Mock
    private BranchInventoryRepository branchInventoryRepo;
    @Mock
    private ProcurementOrderRepository procurementRepo;
    @Mock
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;
    @InjectMocks
    private InventoryService inventoryService;

    private UUID branchId, partId;

    @BeforeEach
    void setUp() {
        branchId = UUID.randomUUID();
        partId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("reserveParts()")
    class ReserveParts {

        @Test
        @DisplayName("should reserve parts and reduce stock")
        void shouldReserve() {
            BranchInventory inv = BranchInventory.builder()
                    .branchId(branchId).sparePartId(partId)
                    .qty(10).minStock(3).build();

            when(branchInventoryRepo.findById(any(BranchInventoryId.class))).thenReturn(Optional.of(inv));
            when(branchInventoryRepo.save(any(BranchInventory.class))).thenAnswer(i -> i.getArgument(0));

            BranchInventory result = inventoryService.reserveParts(branchId, partId, 3);

            assertThat(result.getQty()).isEqualTo(7);
            verify(branchInventoryRepo).save(any(BranchInventory.class));
        }

        @Test
        @DisplayName("should throw exception when insufficient stock")
        void shouldRejectInsufficientStock() {
            BranchInventory inv = BranchInventory.builder()
                    .branchId(branchId).sparePartId(partId)
                    .qty(2).minStock(1).build();

            when(branchInventoryRepo.findById(any(BranchInventoryId.class))).thenReturn(Optional.of(inv));

            assertThatThrownBy(() -> inventoryService.reserveParts(branchId, partId, 5))
                    .isInstanceOf(RepairBroException.class)
                    .hasMessageContaining("Insufficient stock");
        }

        @Test
        @DisplayName("should publish StockLow event when below minStock")
        void shouldPublishLowStockEvent() {
            BranchInventory inv = BranchInventory.builder()
                    .branchId(branchId).sparePartId(partId)
                    .qty(5).minStock(5).build();

            when(branchInventoryRepo.findById(any(BranchInventoryId.class))).thenReturn(Optional.of(inv));
            when(branchInventoryRepo.save(any(BranchInventory.class))).thenAnswer(i -> i.getArgument(0));

            inventoryService.reserveParts(branchId, partId, 3);

            // qty becomes 2, which is <= minStock(5), so StockLow + PartReserved events
            verify(kafkaTemplate, times(2)).send(anyString(), anyString(), any(DomainEvent.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFound for non-existent inventory")
        void shouldThrowNotFound() {
            when(branchInventoryRepo.findById(any(BranchInventoryId.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.reserveParts(branchId, partId, 1))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("placeOrder()")
    class PlaceOrder {

        @Test
        @DisplayName("should save and publish event")
        void shouldPlaceOrder() {
            ProcurementOrder order = ProcurementOrder.builder()
                    .id(UUID.randomUUID()).branchId(branchId).build();

            when(procurementRepo.save(any())).thenReturn(order);

            ProcurementOrder result = inventoryService.placeOrder(order);

            assertThat(result).isNotNull();
            verify(procurementRepo).save(any());
            verify(kafkaTemplate).send(anyString(), anyString(), any(DomainEvent.class));
        }
    }
}
