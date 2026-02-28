package com.repairbro.repaircore.model;

public enum TicketStatus {
    OPEN,
    DIAGNOSING,
    WAITING_FOR_PARTS,
    IN_REPAIR,
    QA_CHECK,
    READY_FOR_PICKUP,
    COMPLETED,
    CANCELLED
}
