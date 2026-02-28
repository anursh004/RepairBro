package com.repairbro.auth.service;

import com.repairbro.auth.dto.*;
import com.repairbro.auth.model.User;
import com.repairbro.auth.model.UserStatus;
import com.repairbro.auth.repository.UserRepository;
import com.repairbro.auth.security.JwtTokenProvider;
import com.repairbro.commons.event.DomainEvent;
import com.repairbro.commons.event.EventTopics;
import com.repairbro.commons.exception.RepairBroException;
import com.repairbro.commons.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RepairBroException(
                    "Email already registered: " + request.getEmail(),
                    HttpStatus.CONFLICT,
                    "EMAIL_EXISTS");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .branchId(request.getBranchId())
                .roles(request.getRoles())
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("User registered: {} [{}]", user.getEmail(), user.getId());

        // Publish domain event
        publishUserEvent("UserCreated", user);

        return generateTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RepairBroException(
                        "Invalid credentials",
                        HttpStatus.UNAUTHORIZED,
                        "INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RepairBroException(
                    "Invalid credentials",
                    HttpStatus.UNAUTHORIZED,
                    "INVALID_CREDENTIALS");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RepairBroException(
                    "Account is " + user.getStatus().name().toLowerCase(),
                    HttpStatus.FORBIDDEN,
                    "ACCOUNT_INACTIVE");
        }

        log.info("User logged in: {} [{}]", user.getEmail(), user.getId());
        return generateTokens(user);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RepairBroException("Invalid refresh token", HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        return generateTokens(user);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
        return toDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────

    private AuthResponse generateTokens(User user) {
        Map<String, Object> claims = new HashMap<>();

        // Legacy roles (kept for backwards compatibility)
        claims.put("roles", user.getRoles().stream().map(Enum::name).toList());

        // New group-based RBAC
        Set<String> groupNames = user.getGroupNames();
        Set<String> permissions = user.getAllPermissions();
        Set<UUID> locationIds = user.getLocationBranchIds();

        claims.put("groups", groupNames.stream().sorted().toList());
        claims.put("permissions", permissions.stream().sorted().toList());
        claims.put("locations", locationIds.stream().map(UUID::toString).sorted().toList());

        UUID primaryBranch = user.getPrimaryBranchId();
        if (primaryBranch != null) {
            claims.put("primaryLocation", primaryBranch.toString());
        }

        // Legacy branchId (kept for backwards compatibility)
        if (user.getBranchId() != null) {
            claims.put("branchId", user.getBranchId().toString());
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs() / 1000)
                .groups(groupNames.stream().sorted().toList())
                .permissions(permissions.stream().sorted().toList())
                .locations(locationIds.stream().map(UUID::toString).sorted().toList())
                .build();
    }

    private void publishUserEvent(String type, User user) {
        try {
            DomainEvent event = DomainEvent.builder()
                    .eventType("repairbro.auth." + type)
                    .aggregateId(user.getId().toString())
                    .aggregateType("User")
                    .source("auth-service")
                    .payload(Map.of(
                            "userId", user.getId().toString(),
                            "email", user.getEmail(),
                            "groups", user.getGroupNames()))
                    .build();
            kafkaTemplate.send(EventTopics.NOTIFICATION_COMMANDS, user.getId().toString(), event);
        } catch (Exception e) {
            log.warn("Failed to publish {} event for user {}: {}", type, user.getId(), e.getMessage());
        }
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .branchId(user.getBranchId())
                .roles(user.getRoles())
                .groups(user.getGroupNames().stream().sorted().toList())
                .permissions(user.getAllPermissions().stream().sorted().toList())
                .locations(user.getLocations().stream()
                        .map(loc -> LocationDTO.builder()
                                .branchId(loc.getBranchId())
                                .primaryLocation(loc.isPrimaryLocation())
                                .build())
                        .toList())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
