package com.repairbro.auth.controller;

import com.repairbro.auth.dto.*;
import com.repairbro.auth.service.UserGroupService;
import com.repairbro.commons.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}")
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService userGroupService;

    // ── Group Assignment ────────────────────────────────

    @GetMapping("/groups")
    @PreAuthorize("hasAuthority('PERM_GROUP_VIEW')")
    public ResponseEntity<ApiResponse<List<PermissionGroupDTO>>> getUserGroups(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(userGroupService.getUserGroups(userId)));
    }

    @PostMapping("/groups")
    @PreAuthorize("hasAuthority('PERM_GROUP_ASSIGN_USER')")
    public ResponseEntity<ApiResponse<Void>> assignGroup(
            @PathVariable UUID userId, @RequestBody GroupAssignRequest request) {
        userGroupService.assignGroupToUser(userId, request.getGroupId());
        return ResponseEntity.ok(ApiResponse.ok(null, "Group assigned to user"));
    }

    @DeleteMapping("/groups/{groupId}")
    @PreAuthorize("hasAuthority('PERM_GROUP_ASSIGN_USER')")
    public ResponseEntity<ApiResponse<Void>> removeGroup(
            @PathVariable UUID userId, @PathVariable UUID groupId) {
        userGroupService.removeGroupFromUser(userId, groupId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Group removed from user"));
    }

    // ── Location Management ─────────────────────────────

    @GetMapping("/locations")
    @PreAuthorize("hasAuthority('PERM_USER_VIEW_ALL')")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getUserLocations(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(userGroupService.getUserLocations(userId)));
    }

    @PostMapping("/locations")
    @PreAuthorize("hasAuthority('PERM_USER_ASSIGN_LOCATION')")
    public ResponseEntity<ApiResponse<Void>> assignLocation(
            @PathVariable UUID userId, @RequestBody LocationAssignRequest request) {
        userGroupService.assignLocationToUser(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(null, "Location assigned to user"));
    }

    @DeleteMapping("/locations/{branchId}")
    @PreAuthorize("hasAuthority('PERM_USER_ASSIGN_LOCATION')")
    public ResponseEntity<ApiResponse<Void>> removeLocation(
            @PathVariable UUID userId, @PathVariable UUID branchId) {
        userGroupService.removeLocationFromUser(userId, branchId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Location removed from user"));
    }

    // ── Effective Permissions ────────────────────────────

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('PERM_GROUP_VIEW')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPermissionSummary(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(userGroupService.getUserPermissionSummary(userId)));
    }
}
