package com.repairbro.auth.service;

import com.repairbro.auth.dto.*;
import com.repairbro.auth.model.*;
import com.repairbro.auth.repository.*;
import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupService {

    private final UserRepository userRepository;
    private final PermissionGroupRepository groupRepository;
    private final UserLocationRepository locationRepository;

    // ── Group Assignment ────────────────────────────────

    @Transactional(readOnly = true)
    public List<PermissionGroupDTO> getUserGroups(UUID userId) {
        User user = findUser(userId);
        return user.getPermissionGroups().stream()
                .map(this::toGroupDTO)
                .toList();
    }

    @Transactional
    public void assignGroupToUser(UUID userId, UUID groupId) {
        User user = findUser(userId);
        PermissionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", groupId.toString()));

        if (!group.isActive()) {
            throw new RepairBroException("Group is inactive: " + group.getName(),
                    HttpStatus.BAD_REQUEST, "GROUP_INACTIVE");
        }

        user.getPermissionGroups().add(group);
        userRepository.save(user);
        log.info("Group {} assigned to user {}", group.getName(), user.getEmail());
    }

    @Transactional
    public void removeGroupFromUser(UUID userId, UUID groupId) {
        User user = findUser(userId);
        user.getPermissionGroups().removeIf(g -> g.getId().equals(groupId));
        userRepository.save(user);
        log.info("Group {} removed from user {}", groupId, user.getEmail());
    }

    // ── Location Management ─────────────────────────────

    @Transactional(readOnly = true)
    public List<LocationDTO> getUserLocations(UUID userId) {
        return locationRepository.findByUserId(userId).stream()
                .map(loc -> LocationDTO.builder()
                        .branchId(loc.getBranchId())
                        .primaryLocation(loc.isPrimaryLocation())
                        .build())
                .toList();
    }

    @Transactional
    public void assignLocationToUser(UUID userId, LocationAssignRequest request) {
        findUser(userId); // validate user exists

        Optional<UserLocation> existing = locationRepository
                .findByUserIdAndBranchId(userId, request.getBranchId());

        if (existing.isPresent()) {
            UserLocation loc = existing.get();
            loc.setPrimaryLocation(request.isPrimaryLocation());
            locationRepository.save(loc);
        } else {
            // If this is primary, unset any existing primary
            if (request.isPrimaryLocation()) {
                locationRepository.findByUserId(userId).forEach(loc -> {
                    if (loc.isPrimaryLocation()) {
                        loc.setPrimaryLocation(false);
                        locationRepository.save(loc);
                    }
                });
            }

            UserLocation loc = UserLocation.builder()
                    .userId(userId)
                    .branchId(request.getBranchId())
                    .primaryLocation(request.isPrimaryLocation())
                    .build();
            locationRepository.save(loc);
        }
        log.info("Location {} assigned to user {} (primary={})",
                request.getBranchId(), userId, request.isPrimaryLocation());
    }

    @Transactional
    public void removeLocationFromUser(UUID userId, UUID branchId) {
        locationRepository.deleteByUserIdAndBranchId(userId, branchId);
        log.info("Location {} removed from user {}", branchId, userId);
    }

    // ── Effective Permissions ────────────────────────────

    @Transactional(readOnly = true)
    public Set<String> getEffectivePermissions(UUID userId) {
        User user = findUser(userId);
        return user.getAllPermissions();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserPermissionSummary(UUID userId) {
        User user = findUser(userId);
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("userId", userId);
        summary.put("email", user.getEmail());
        summary.put("groups", user.getGroupNames());
        summary.put("effectivePermissions", user.getAllPermissions().stream().sorted().toList());
        summary.put("locations", user.getLocations().stream()
                .map(l -> Map.of("branchId", l.getBranchId(), "primary", l.isPrimaryLocation()))
                .toList());
        return summary;
    }

    // ── Helpers ──────────────────────────────────────────

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private PermissionGroupDTO toGroupDTO(PermissionGroup group) {
        return PermissionGroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .level(group.getLevel())
                .systemDefined(group.isSystemDefined())
                .active(group.isActive())
                .permissions(group.getPermissions().stream()
                        .map(p -> PermissionDTO.builder()
                                .id(p.getId())
                                .code(p.getCode())
                                .name(p.getName())
                                .module(p.getModule())
                                .build())
                        .toList())
                .build();
    }
}
