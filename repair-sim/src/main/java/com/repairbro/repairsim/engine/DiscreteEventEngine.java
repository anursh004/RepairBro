package com.repairbro.repairsim.engine;

import com.repairbro.repairsim.model.SimulationResultEntity;
import com.repairbro.repairsim.model.SimulationScenario;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Discrete-event simulation engine for repair shop operations.
 * Simulates ticket arrivals, technician allocation, parts ordering,
 * repair execution, and QA — producing P&L and KPI outputs.
 */
@Slf4j
public class DiscreteEventEngine {

    private final SimulationScenario scenario;
    private final PriorityQueue<SimEvent> eventQueue = new PriorityQueue<>();

    // State
    private int currentDay = 0;
    private int availableTechs;
    private int totalTickets = 0;
    private int completedTickets = 0;
    private int firstTimeFixCount = 0;
    private int slaBreaches = 0;
    private int stockouts = 0;
    private double totalRepairTimeHours = 0;
    private double totalRevenue = 0;
    private double totalPartsCost = 0;
    private double totalLaborCost = 0;
    private double totalTechBusyHours = 0;
    private final Map<Integer, Integer> partsInventory = new HashMap<>(); // partId -> qty

    record SimEvent(double time, EventType type, Map<String, Object> data) implements Comparable<SimEvent> {
        @Override
        public int compareTo(SimEvent o) {
            return Double.compare(this.time, o.time);
        }
    }

    enum EventType {
        TICKET_ARRIVAL, DIAGNOSIS_START, PARTS_CHECK, REPAIR_START, QA_CHECK, COMPLETION
    }

    public DiscreteEventEngine(SimulationScenario scenario) {
        this.scenario = scenario;
        this.availableTechs = scenario.getTechsPerBranch();
        // Initialize inventory: 20 types of parts, 15 units each
        for (int i = 0; i < 20; i++) {
            partsInventory.put(i, 15);
        }
    }

    public SimulationResultEntity runForBranch(int branchIndex) {
        // Generate ticket arrivals using Poisson distribution
        generateArrivals();

        // Process event queue
        while (!eventQueue.isEmpty()) {
            SimEvent event = eventQueue.poll();
            processEvent(event);
        }

        return buildResult(branchIndex);
    }

    private void generateArrivals() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int day = 0; day < scenario.getSimulationDays(); day++) {
            // Poisson arrivals: use normal approximation for λ > 10
            int dailyDemand = Math.max(1, (int) (scenario.getAvgDemandsPerDay()
                    + rng.nextGaussian() * Math.sqrt(scenario.getAvgDemandsPerDay())));

            for (int i = 0; i < dailyDemand; i++) {
                double arrivalTime = day * 24.0 + rng.nextDouble() * 10 + 8; // 8am-6pm
                eventQueue.add(new SimEvent(arrivalTime, EventType.TICKET_ARRIVAL,
                        Map.of("ticketNum", totalTickets++, "day", day,
                                "priority", rng.nextDouble() < 0.2 ? "URGENT" : "NORMAL",
                                "needsParts", rng.nextDouble() < 0.6)));
            }
        }
        log.debug("Generated {} ticket arrivals over {} days", totalTickets, scenario.getSimulationDays());
    }

    private void processEvent(SimEvent event) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        switch (event.type) {
            case TICKET_ARRIVAL -> {
                // Schedule diagnosis
                double diagDelay = availableTechs > 0 ? 0.5 : rng.nextDouble() * 4 + 2; // wait if no techs
                if (availableTechs > 0)
                    availableTechs--;
                eventQueue.add(new SimEvent(event.time + diagDelay, EventType.DIAGNOSIS_START, event.data));
            }
            case DIAGNOSIS_START -> {
                double diagTime = isJuniorTech() ? rng.nextDouble() * 1.5 + 1.0 : rng.nextDouble() * 0.8 + 0.5;
                totalTechBusyHours += diagTime;
                eventQueue.add(new SimEvent(event.time + diagTime, EventType.PARTS_CHECK, event.data));
            }
            case PARTS_CHECK -> {
                boolean needsParts = (boolean) event.data.get("needsParts");
                if (needsParts) {
                    int partId = rng.nextInt(20);
                    int stock = partsInventory.getOrDefault(partId, 0);
                    if (stock > 0) {
                        partsInventory.put(partId, stock - 1);
                        totalPartsCost += scenario.getAvgPartsCost() * (0.8 + rng.nextDouble() * 0.4);
                        eventQueue.add(new SimEvent(event.time + 0.5, EventType.REPAIR_START, event.data));
                    } else {
                        stockouts++;
                        // Wait for parts delivery (24-72 hours)
                        double waitTime = scenario.getInventoryStrategy().equals("CENTRALIZED")
                                ? rng.nextDouble() * 24 + 12
                                : rng.nextDouble() * 48 + 24;
                        partsInventory.put(partId, 5); // Restock
                        totalPartsCost += scenario.getAvgPartsCost() * 1.3; // Premium pricing
                        eventQueue.add(new SimEvent(event.time + waitTime, EventType.REPAIR_START, event.data));
                    }
                } else {
                    eventQueue.add(new SimEvent(event.time + 0.2, EventType.REPAIR_START, event.data));
                }
            }
            case REPAIR_START -> {
                double repairTime = isJuniorTech() ? rng.nextDouble() * 3 + 2 : rng.nextDouble() * 2 + 1;
                totalTechBusyHours += repairTime;
                totalLaborCost += repairTime * (isJuniorTech() ? 200 : 350); // hourly rate
                eventQueue.add(new SimEvent(event.time + repairTime, EventType.QA_CHECK, event.data));
            }
            case QA_CHECK -> {
                double qaTime = 0.5;
                totalTechBusyHours += qaTime;
                boolean passQA = rng.nextDouble() < (isJuniorTech() ? 0.75 : 0.92);
                if (passQA) {
                    eventQueue.add(new SimEvent(event.time + qaTime, EventType.COMPLETION, event.data));
                } else {
                    // Rework — goes back to repair
                    eventQueue.add(new SimEvent(event.time + qaTime, EventType.REPAIR_START, event.data));
                }
            }
            case COMPLETION -> {
                completedTickets++;
                double ticketRevenue = scenario.getAvgTicketValue() * (0.7 + rng.nextDouble() * 0.6);
                totalRevenue += ticketRevenue;

                double repairTime = event.time - (int) (event.time / 24) * 24; // hours within day
                totalRepairTimeHours += Math.max(1, repairTime);

                // First-time fix if completed same day
                boolean priority = "URGENT".equals(event.data.get("priority"));
                double slaDeadline = priority ? 24 : 48;
                if (repairTime <= slaDeadline) {
                    firstTimeFixCount++;
                } else {
                    slaBreaches++;
                }
                availableTechs = Math.min(availableTechs + 1, scenario.getTechsPerBranch());
            }
        }
    }

    private boolean isJuniorTech() {
        return ThreadLocalRandom.current().nextDouble() < scenario.getJuniorTechRatio();
    }

    private SimulationResultEntity buildResult(int branchIndex) {
        double totalRent = (scenario.getMonthlyRent() / 30.0) * scenario.getSimulationDays();
        double royaltyRate = 0.07;
        double totalRoyalty = totalRevenue * royaltyRate;
        double netProfit = totalRevenue - totalPartsCost - totalLaborCost - totalRent - totalRoyalty;
        double profitMargin = totalRevenue > 0 ? (netProfit / totalRevenue) * 100 : 0;
        double ftfr = completedTickets > 0 ? (double) firstTimeFixCount / completedTickets : 0;
        double mttr = completedTickets > 0 ? totalRepairTimeHours / completedTickets : 0;
        double totalAvailHours = scenario.getTechsPerBranch() * scenario.getSimulationDays() * 8.0;
        double utilization = totalAvailHours > 0 ? (totalTechBusyHours / totalAvailHours) * 100 : 0;

        // Break-even: daily fixed costs / (avg revenue per ticket - avg variable cost
        // per ticket)
        double dailyFixed = (totalRent + totalRoyalty) / scenario.getSimulationDays();
        double avgVarCost = completedTickets > 0
                ? (totalPartsCost + totalLaborCost) / completedTickets
                : 0;
        double avgRev = completedTickets > 0 ? totalRevenue / completedTickets : scenario.getAvgTicketValue();
        int breakEven = avgRev > avgVarCost
                ? (int) Math.ceil(dailyFixed / (avgRev - avgVarCost))
                : 999;

        return SimulationResultEntity.builder()
                .branchIndex(branchIndex)
                .totalRevenue(Math.round(totalRevenue))
                .totalPartsCost(Math.round(totalPartsCost))
                .totalLaborCost(Math.round(totalLaborCost))
                .totalRent(Math.round(totalRent))
                .totalRoyalty(Math.round(totalRoyalty))
                .netProfit(Math.round(netProfit))
                .profitMarginPercent(Math.round(profitMargin * 10.0) / 10.0)
                .totalTickets(totalTickets)
                .completedTickets(completedTickets)
                .slaBreach(slaBreaches)
                .firstTimeFixRate(Math.round(ftfr * 1000.0) / 1000.0)
                .meanRepairTimeHours(Math.round(mttr * 10.0) / 10.0)
                .techUtilizationPercent(Math.round(utilization * 10.0) / 10.0)
                .inventoryStockouts(stockouts)
                .breakEvenTicketsPerDay(breakEven)
                .build();
    }
}
