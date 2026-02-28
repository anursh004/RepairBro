package com.repairbro.auth.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true, length = 128)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 128)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /** @deprecated Use user_locations instead. Kept for backwards compatibility. */
    @Deprecated
    private UUID branchId;

    /**
     * @deprecated Use permission groups instead. Kept for backwards compatibility.
     */
    @Deprecated
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // ── New Group-based RBAC ────────────────────────────

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_groups", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    @Builder.Default
    private Set<PermissionGroup> permissionGroups = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @Builder.Default
    private Set<UserLocation> locations = new HashSet<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    // ── Helper methods ──────────────────────────────────

    /**
     * Flatten all permissions from all groups into a unique set of permission
     * codes.
     */
    public Set<String> getAllPermissions() {
        return permissionGroups.stream()
                .flatMap(g -> g.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    /** Get names of all assigned groups. */
    public Set<String> getGroupNames() {
        return permissionGroups.stream()
                .map(PermissionGroup::getName)
                .collect(Collectors.toSet());
    }

    /** Get all branch IDs the user is mapped to. */
    public Set<UUID> getLocationBranchIds() {
        return locations.stream()
                .map(UserLocation::getBranchId)
                .collect(Collectors.toSet());
    }

    /** Get the primary location or first available. */
    public UUID getPrimaryBranchId() {
        return locations.stream()
                .filter(UserLocation::isPrimaryLocation)
                .map(UserLocation::getBranchId)
                .findFirst()
                .orElse(locations.stream()
                        .map(UserLocation::getBranchId)
                        .findFirst()
                        .orElse(branchId)); // fallback to legacy field
    }
}
