package com.repairbro.auth.service;

import com.repairbro.auth.dto.CreateTechnicianRequest;
import com.repairbro.auth.dto.TechnicianDTO;
import com.repairbro.auth.model.TechnicianProfile;
import com.repairbro.auth.model.User;
import com.repairbro.auth.repository.TechnicianRepository;
import com.repairbro.auth.repository.UserRepository;
import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TechnicianService {

    private final TechnicianRepository techRepo;
    private final UserRepository userRepo;

    @Transactional
    public TechnicianDTO createProfile(CreateTechnicianRequest request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId().toString()));

        if (techRepo.findByUserId(request.getUserId()).isPresent()) {
            throw new RepairBroException(
                    "Technician profile already exists for user " + request.getUserId(),
                    HttpStatus.CONFLICT, "DUPLICATE_PROFILE");
        }

        TechnicianProfile profile = TechnicianProfile.builder()
                .userId(request.getUserId())
                .branchId(request.getBranchId())
                .skillLevel(request.getSkillLevel())
                .specializations(request.getSpecializations())
                .certifications(request.getCertifications())
                .hourlyRate(request.getHourlyRate() != null ? request.getHourlyRate() : BigDecimal.ZERO)
                .build();

        profile = techRepo.save(profile);
        log.info("Technician profile created for user {} at branch {}", user.getFullName(), profile.getBranchId());
        return toDTO(profile, user);
    }

    @Transactional(readOnly = true)
    public TechnicianDTO getByUserId(UUID userId) {
        TechnicianProfile profile = techRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TechnicianProfile", "userId=" + userId));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
        return toDTO(profile, user);
    }

    @Transactional(readOnly = true)
    public List<TechnicianDTO> getByBranch(UUID branchId) {
        return techRepo.findByBranchIdAndActiveTrue(branchId).stream()
                .map(p -> {
                    User u = userRepo.findById(p.getUserId()).orElse(null);
                    return toDTO(p, u);
                }).toList();
    }

    @Transactional(readOnly = true)
    public List<TechnicianDTO> getAll() {
        return techRepo.findAll().stream()
                .map(p -> {
                    User u = userRepo.findById(p.getUserId()).orElse(null);
                    return toDTO(p, u);
                }).toList();
    }

    @Transactional
    public TechnicianDTO updateProfile(UUID profileId, CreateTechnicianRequest request) {
        TechnicianProfile profile = techRepo.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("TechnicianProfile", profileId.toString()));
        profile.setBranchId(request.getBranchId());
        profile.setSkillLevel(request.getSkillLevel());
        profile.setSpecializations(request.getSpecializations());
        profile.setCertifications(request.getCertifications());
        if (request.getHourlyRate() != null) {
            profile.setHourlyRate(request.getHourlyRate());
        }
        profile = techRepo.save(profile);
        User user = userRepo.findById(profile.getUserId()).orElse(null);
        return toDTO(profile, user);
    }

    @Transactional
    public void recordTicketCompletion(UUID userId, double repairTimeHours, boolean firstTimeFix) {
        TechnicianProfile profile = techRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("TechnicianProfile", "userId=" + userId));

        int total = profile.getTicketsResolved() + 1;
        double newAvg = ((profile.getAvgRepairTimeHours() * profile.getTicketsResolved()) + repairTimeHours) / total;
        double ftfCount = profile.getFirstTimeFixRate() * profile.getTicketsResolved();
        double newFtfr = (ftfCount + (firstTimeFix ? 1 : 0)) / total;

        profile.setTicketsResolved(total);
        profile.setAvgRepairTimeHours(Math.round(newAvg * 100.0) / 100.0);
        profile.setFirstTimeFixRate(Math.round(newFtfr * 1000.0) / 1000.0);
        techRepo.save(profile);
        log.info("Tech {} completed ticket. Total: {}, FTFR: {}", userId, total, profile.getFirstTimeFixRate());
    }

    private TechnicianDTO toDTO(TechnicianProfile p, User u) {
        return TechnicianDTO.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .fullName(u != null ? u.getFullName() : null)
                .email(u != null ? u.getEmail() : null)
                .branchId(p.getBranchId())
                .skillLevel(p.getSkillLevel())
                .specializations(p.getSpecializations())
                .certifications(p.getCertifications())
                .hourlyRate(p.getHourlyRate())
                .ticketsResolved(p.getTicketsResolved())
                .avgRepairTimeHours(p.getAvgRepairTimeHours())
                .firstTimeFixRate(p.getFirstTimeFixRate())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
