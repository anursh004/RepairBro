import { createContext, useContext, useState, useEffect } from 'react';
import { ROLE_ACCESS } from '../utils/constants';
import { SECTION_PERMISSIONS, GROUP_LEVEL_MAP, GROUP_LEVEL_PRIORITY } from '../config/permissions.config';

const AuthContext = createContext(null);

/**
 * Derive the highest-privilege group level from the user's group names.
 * E.g., a user in ["Service Manager", "Billing Viewer"] => "MANAGEMENT"
 */
function resolveGroupLevel(groups = []) {
    for (const level of GROUP_LEVEL_PRIORITY) {
        if (groups.some((g) => GROUP_LEVEL_MAP[g] === level)) {
            return level;
        }
    }
    return 'OPERATIONAL'; // fallback
}

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Restore session from sessionStorage
        const stored = sessionStorage.getItem('rb_user');
        if (stored) {
            try {
                setUser(JSON.parse(stored));
            } catch { /* ignore */ }
        }
        setLoading(false);
    }, []);

    const login = (userData, token, refreshToken) => {
        const groups = userData.groups || [];
        const locations = userData.locations || [];
        const u = {
            id: userData.userId || userData.id,
            name: userData.fullName || userData.name || userData.email,
            email: userData.email,
            role: userData.role || 'ADMIN',
            // New group-based RBAC fields
            groups,
            permissions: userData.permissions || [],
            locations,
            primaryLocation: userData.primaryLocation || locations[0] || null,
            groupLevel: resolveGroupLevel(groups),
        };
        sessionStorage.setItem('rb_token', token);
        if (refreshToken) sessionStorage.setItem('rb_refresh', refreshToken);
        sessionStorage.setItem('rb_user', JSON.stringify(u));
        setUser(u);
    };

    const logout = () => {
        sessionStorage.clear();
        setUser(null);
    };

    /**
     * Check if user can access a given nav section.
     * Uses permission-based check first; falls back to legacy role-based check.
     */
    const hasAccess = (section) => {
        if (!user) return false;

        // Permission-based check (new system)
        if (user.permissions && user.permissions.length > 0) {
            const required = SECTION_PERMISSIONS[section];
            if (!required) return false;
            return required.some(perm => user.permissions.includes(perm));
        }

        // Fallback to legacy role-based check
        const access = ROLE_ACCESS[user.role] || [];
        return access.includes(section);
    };

    /**
     * Check if user has a specific API-level permission.
     */
    const hasPermission = (permCode) => {
        if (!user) return false;
        if (user.permissions && user.permissions.length > 0) {
            return user.permissions.includes(permCode);
        }
        // Legacy fallback: admins have all permissions
        return user.role === 'ADMIN';
    };

    /**
     * Check if user has ANY ONE of the given permissions.
     */
    const hasAnyPermission = (perms = []) => {
        if (!user) return false;
        return perms.some((p) => hasPermission(p));
    };

    /**
     * Check if user belongs to a specific group.
     */
    const inGroup = (groupName) => {
        if (!user) return false;
        return (user.groups || []).includes(groupName);
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, logout, hasAccess, hasPermission, hasAnyPermission, inGroup }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
}
