package com.repairbro.auth.controller;

import com.repairbro.auth.dto.*;
import com.repairbro.auth.service.PermissionGroupService;
import com.repairbro.commons.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PermissionGroupController {

    private final PermissionGroupService groupService;

    // ── Permission Group CRUD ────────────────────────────

    @GetMapping("/groups")
    @PreAuthorize("hasAuthority('PERM_GROUP_VIEW')")
    public ResponseEntity<ApiResponse<List<PermissionGroupDTO>>> getAllGroups() {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getActiveGroups()));
    }

    @GetMapping("/groups/{id}")
    @PreAuthorize("hasAuthority('PERM_GROUP_VIEW')")
    public ResponseEntity<ApiResponse<PermissionGroupDTO>> getGroup(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getGroupById(id)));
    }

    @PostMapping("/groups")
    @PreAuthorize("hasAuthority('PERM_GROUP_CREATE')")
    public ResponseEntity<ApiResponse<PermissionGroupDTO>> createGroup(@RequestBody CreateGroupRequest request) {
        PermissionGroupDTO group = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(group, "Permission group created"));
    }

    @PutMapping("/groups/{id}")
    @PreAuthorize("hasAuthority('PERM_GROUP_EDIT')")
    public ResponseEntity<ApiResponse<PermissionGroupDTO>> updateGroup(
            @PathVariable UUID id, @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(groupService.updateGroup(id, request), "Group updated"));
    }

    @DeleteMapping("/groups/{id}")
    @PreAuthorize("hasAuthority('PERM_GROUP_DELETE')")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Group deleted"));
    }

    // ── Permission Management within Groups ──────────────

    @PostMapping("/groups/{groupId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('PERM_GROUP_EDIT')")
    public ResponseEntity<ApiResponse<PermissionGroupDTO>> addPermission(
            @PathVariable UUID groupId, @PathVariable UUID permissionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupService.addPermissionToGroup(groupId, permissionId), "Permission added"));
    }

    @DeleteMapping("/groups/{groupId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('PERM_GROUP_EDIT')")
    public ResponseEntity<ApiResponse<PermissionGroupDTO>> removePermission(
            @PathVariable UUID groupId, @PathVariable UUID permissionId) {
        return ResponseEntity.ok(ApiResponse.ok(
                groupService.removePermissionFromGroup(groupId, permissionId), "Permission removed"));
    }

    // ── Permission Catalog ───────────────────────────────

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('PERM_GROUP_VIEW')")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getAllPermissions() {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getAllPermissions()));
    }

    @GetMapping("/permissions/modules")
    @PreAuthorize("hasAuthority('PERM_GROUP_VIEW')")
    public ResponseEntity<ApiResponse<List<String>>> getModules() {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getPermissionModules()));
    }

    @GetMapping("/permissions/modules/{module}")
    @PreAuthorize("hasAuthority('PERM_GROUP_VIEW')")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getByModule(@PathVariable String module) {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getPermissionsByModule(module)));
    }
}
