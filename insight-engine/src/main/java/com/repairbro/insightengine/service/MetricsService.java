package com.repairbro.insightengine.service;

import com.repairbro.insightengine.model.BranchKpiDaily;
import com.repairbro.insightengine.repository.BranchKpiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final BranchKpiRepository kpiRepo;

    @Transactional(readOnly = true)
    public List<BranchKpiDaily> getBranchMetrics(UUID branchId, LocalDate from, LocalDate to) {
        return kpiRepo.findByBranchIdAndDateBetween(branchId, from, to);
    }

    @Transactional(readOnly = true)
    public List<BranchKpiDaily> getDailyReport(LocalDate date) {
        return kpiRepo.findByDate(date);
    }

    @Transactional
    public BranchKpiDaily saveKpi(BranchKpiDaily kpi) {
        return kpiRepo.save(kpi);
    }
}
