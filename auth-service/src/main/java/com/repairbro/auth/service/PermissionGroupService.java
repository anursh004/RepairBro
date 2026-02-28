package com.repairbro.auth.service;

import com.repairbro.auth.dto.*;
import com.repairbro.auth.model.Permission;
import com.repairbro.auth.model.PermissionGroup;
import com.repairbro.auth.repository.PermissionGroupRepository;
import com.repairbro.auth.repository.PermissionRepository;
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
public class PermissionGroupService {

    private final PermissionGroupRepository groupRepository;
    private final PermissionRepository permissionRepository;

    // ── Queries ─────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PermissionGroupDTO> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PermissionGroupDTO> getActiveGroups() {
        return groupRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionGroupDTO getGroupById(UUID id) {
        PermissionGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", id.toString()));
        return toDTO(group);
    }

    @Transactional(readOnly = true)
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAllByOrderByModuleAscCodeAsc().stream()
                .map(this::toPermissionDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getPermissionModules() {
        return permissionRepository.findAllByOrderByModuleAscCodeAsc().stream()
                .map(Permission::getModule)
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PermissionDTO> getPermissionsByModule(String module) {
        return permissionRepository.findByModule(module).stream()
                .map(this::toPermissionDTO)
                .toList();
    }

    // ── Mutations ───────────────────────────────────────

    @Transactional
    public PermissionGroupDTO createGroup(CreateGroupRequest request) {
        if (groupRepository.existsByName(request.getName())) {
            throw new RepairBroException(
                    "Group name already exists: " + request.getName(),
                    HttpStatus.CONFLICT, "GROUP_EXISTS");
        }

        PermissionGroup group = PermissionGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .level(request.getLevel() != null ? request.getLevel() : "OPERATIONAL")
                .systemDefined(false)
                .active(true)
                .build();

        if (request.getPermissionCodes() != null && !request.getPermissionCodes().isEmpty()) {
            Set<Permission> perms = resolvePermissions(request.getPermissionCodes());
            group.setPermissions(perms);
        }

        group = groupRepository.save(group);
        log.info("Permission group created: {} [{}]", group.getName(), group.getId());
        return toDTO(group);
    }

    @Transactional
    public PermissionGroupDTO updateGroup(UUID groupId, CreateGroupRequest request) {
        PermissionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", groupId.toString()));

        if (group.isSystemDefined()) {
            // Allow editing permissions of system groups, but not name/level
            if (request.getPermissionCodes() != null) {
                group.setPermissions(resolvePermissions(request.getPermissionCodes()));
            }
            if (request.getDescription() != null) {
                group.setDescription(request.getDescription());
            }
        } else {
            if (request.getName() != null)
                group.setName(request.getName());
            if (request.getDescription() != null)
                group.setDescription(request.getDescription());
            if (request.getLevel() != null)
                group.setLevel(request.getLevel());
            if (request.getPermissionCodes() != null) {
                group.setPermissions(resolvePermissions(request.getPermissionCodes()));
            }
        }

        group = groupRepository.save(group);
        log.info("Permission group updated: {} [{}]", group.getName(), group.getId());
        return toDTO(group);
    }

    @Transactional
    public void deleteGroup(UUID groupId) {
        PermissionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", groupId.toString()));

        if (group.isSystemDefined()) {
            throw new RepairBroException(
                    "Cannot delete system-defined group: " + group.getName(),
                    HttpStatus.FORBIDDEN, "SYSTEM_GROUP");
        }

        groupRepository.delete(group);
        log.info("Permission group deleted: {} [{}]", group.getName(), group.getId());
    }

    @Transactional
    public PermissionGroupDTO addPermissionToGroup(UUID groupId, UUID permissionId) {
        PermissionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", groupId.toString()));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", permissionId.toString()));

        group.getPermissions().add(permission);
        group = groupRepository.save(group);
        log.info("Permission {} added to group {}", permission.getCode(), group.getName());
        return toDTO(group);
    }

    @Transactional
    public PermissionGroupDTO removePermissionFromGroup(UUID groupId, UUID permissionId) {
        PermissionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", groupId.toString()));

        group.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        group = groupRepository.save(group);
        log.info("Permission {} removed from group {}", permissionId, group.getName());
        return toDTO(group);
    }

    // ── Helpers ──────────────────────────────────────────

    private Set<Permission> resolvePermissions(Set<String> codes) {
        Set<Permission> perms = new HashSet<>();
        for (String code : codes) {
            Permission p = permissionRepository.findByCode(code)
                    .orElseThrow(() -> new ResourceNotFoundException("Permission", code));
            perms.add(p);
        }
        return perms;
    }

    private PermissionGroupDTO toDTO(PermissionGroup group) {
        return PermissionGroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .level(group.getLevel())
                .systemDefined(group.isSystemDefined())
                .active(group.isActive())
                .permissions(group.getPermissions().stream()
                        .map(this::toPermissionDTO)
                        .sorted(Comparator.comparing(PermissionDTO::getModule)
                                .thenComparing(PermissionDTO::getCode))
                        .toList())
                .build();
    }

    private PermissionDTO toPermissionDTO(Permission p) {
        return PermissionDTO.builder()
                .id(p.getId())
                .code(p.getCode())
                .name(p.getName())
                .module(p.getModule())
                .httpMethod(p.getHttpMethod())
                .apiPattern(p.getApiPattern())
                .description(p.getDescription())
                .build();
    }
}
