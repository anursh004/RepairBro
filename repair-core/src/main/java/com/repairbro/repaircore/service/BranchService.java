package com.repairbro.repaircore.service;

import com.repairbro.commons.exception.ResourceNotFoundException;
import com.repairbro.repaircore.dto.BranchDTO;
import com.repairbro.repaircore.dto.CreateBranchRequest;
import com.repairbro.repaircore.model.Branch;
import com.repairbro.repaircore.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepo;

    @Transactional
    @CacheEvict(value = "branches", allEntries = true)
    public BranchDTO createBranch(CreateBranchRequest request) {
        Branch branch = Branch.builder()
                .name(request.getName())
                .city(request.getCity())
                .tier(request.getTier())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .monthlyRent(request.getMonthlyRent())
                .build();

        branch = branchRepo.save(branch);
        log.info("Branch created: {} [{}] in {} (Tier-{})",
                branch.getName(), branch.getId(), branch.getCity(), branch.getTier());
        return toDTO(branch);
    }

    @Transactional(readOnly = true)
    public BranchDTO getBranch(UUID id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "branches", key = "'all-active'")
    public List<BranchDTO> getActiveBranches() {
        return branchRepo.findByActiveTrue().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<BranchDTO> getByCity(String city) {
        return branchRepo.findByCityAndActiveTrue(city).stream()
                .map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<BranchDTO> getByTier(int tier) {
        return branchRepo.findByTier(tier).stream().map(this::toDTO).toList();
    }

    @Transactional
    @CacheEvict(value = "branches", allEntries = true)
    public BranchDTO updateBranch(UUID id, CreateBranchRequest request) {
        Branch branch = findOrThrow(id);
        branch.setName(request.getName());
        branch.setCity(request.getCity());
        branch.setTier(request.getTier());
        branch.setAddress(request.getAddress());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setMonthlyRent(request.getMonthlyRent());
        branch = branchRepo.save(branch);
        return toDTO(branch);
    }

    @Transactional
    @CacheEvict(value = "branches", allEntries = true)
    public void deactivateBranch(UUID id) {
        Branch branch = findOrThrow(id);
        branch.setActive(false);
        branchRepo.save(branch);
        log.info("Branch deactivated: {}", id);
    }

    public Branch findOrThrow(UUID id) {
        return branchRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id.toString()));
    }

    private BranchDTO toDTO(Branch b) {
        return BranchDTO.builder()
                .id(b.getId())
                .name(b.getName())
                .city(b.getCity())
                .tier(b.getTier())
                .address(b.getAddress())
                .phone(b.getPhone())
                .email(b.getEmail())
                .monthlyRent(b.getMonthlyRent())
                .active(b.isActive())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
