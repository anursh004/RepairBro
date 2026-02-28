package com.repairbro.partsflow.service;

import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.partsflow.model.*;
import com.repairbro.partsflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final SparePartRepository sparePartRepo;
    private final BranchInventoryRepository branchInventoryRepo;
    private final ProcurementOrderRepository procurementRepo;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Transactional(readOnly = true)
    public List<SparePart> getAllParts() {
        return sparePartRepo.findAll();
    }

    @Transactional
    public SparePart createPart(SparePart part) {
        return sparePartRepo.save(part);
    }

    @Transactional(readOnly = true)
    public List<BranchInventory> getBranchInventory(UUID branchId) {
        return branchInventoryRepo.findByBranchId(branchId);
    }

    @Transactional(readOnly = true)
    public List<BranchInventory> getLowStock(UUID branchId) {
        return branchInventoryRepo.findLowStock(branchId);
    }

    @Transactional
    public BranchInventory reserveParts(UUID branchId, UUID sparePartId, int qty) {
        BranchInventoryId id = new BranchInventoryId(branchId, sparePartId);
        BranchInventory inv = branchInventoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", branchId + "/" + sparePartId));

        if (inv.getQty() < qty) {
            throw new RepairBroException(
                    "Insufficient stock. Available: " + inv.getQty() + ", requested: " + qty,
                    HttpStatus.CONFLICT, "INSUFFICIENT_STOCK");
        }

        inv.setQty(inv.getQty() - qty);
        inv = branchInventoryRepo.save(inv);

        if (inv.getQty() <= inv.getMinStock()) {
            publishEvent("StockLow", Map.of(
                    "branchId", branchId.toString(),
                    "sparePartId", sparePartId.toString(),
                    "remainingQty", String.valueOf(inv.getQty())));
        }

        publishEvent("PartReserved", Map.of(
                "branchId", branchId.toString(),
                "sparePartId", sparePartId.toString(),
                "reservedQty", String.valueOf(qty)));

        return inv;
    }

    @Transactional
    public ProcurementOrder placeOrder(ProcurementOrder order) {
        order = procurementRepo.save(order);
        publishEvent("OrderPlaced", Map.of(
                "orderId", order.getId().toString(),
                "branchId", order.getBranchId().toString()));
        return order;
    }

    private void publishEvent(String type, Map<String, String> payload) {
        try {
            DomainEvent event = DomainEvent.builder()
                    .eventType("repairbro.parts-flow." + type)
                    .aggregateType("Inventory")
                    .source("parts-flow")
                    .payload(Map.copyOf(payload))
                    .build();
            kafkaTemplate.send(EventTopics.INVENTORY_EVENTS, event.getEventId(), event);
        } catch (Exception e) {
            log.warn("Failed to publish {}: {}", type, e.getMessage());
        }
    }
}
