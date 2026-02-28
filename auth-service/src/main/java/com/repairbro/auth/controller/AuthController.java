package com.repairbro.auth.controller;

import com.repairbro.auth.dto.*;
import com.repairbro.auth.service.AuthService;
import com.repairbro.commons.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── Auth Endpoints (Public — no @PreAuthorize) ────────

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response, "User registered successfully"));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        AuthResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok(response, "Token refreshed"));
    }

    // ── User Management Endpoints (Admin only) ───────────

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('PERM_USER_VIEW_ALL')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = authService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('PERM_USER_VIEW_ALL')")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable UUID userId) {
        UserDTO user = authService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
}
