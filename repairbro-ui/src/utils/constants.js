import { SECTION_PERMISSIONS } from '../config/permissions.config';

// ── Legacy Roles (deprecated — use groups/permissions) ──
export const ROLES = {
    ADMIN: 'ADMIN',
    BRANCH_MANAGER: 'BRANCH_MANAGER',
    TECHNICIAN: 'TECHNICIAN',
    FRANCHISEE: 'FRANCHISEE',
    CUSTOMER: 'CUSTOMER',
};

// ── Legacy role-based access (fallback for old tokens) ──
export const ROLE_ACCESS = {
    [ROLES.ADMIN]: Object.keys(SECTION_PERMISSIONS),
    [ROLES.BRANCH_MANAGER]: ['dashboard', 'tickets', 'customers', 'branches', 'inventory', 'billing', 'diagnostics', 'sla', 'notifications', 'technicians'],
    [ROLES.TECHNICIAN]: ['dashboard', 'tickets', 'diagnostics', 'inventory'],
    [ROLES.FRANCHISEE]: ['dashboard', 'tickets', 'billing', 'franchises', 'sla'],
    [ROLES.CUSTOMER]: ['tickets'],
};

// ── Ticket statuses ─────────────────────────────────────
export const TICKET_STATUS = {
    OPEN: { label: 'Open', color: '#3b82f6' },
    DIAGNOSED: { label: 'Diagnosed', color: '#8b5cf6' },
    AWAITING_PARTS: { label: 'Awaiting Parts', color: '#f59e0b' },
    IN_REPAIR: { label: 'In Repair', color: '#f97316' },
    QA: { label: 'QA Check', color: '#06b6d4' },
    COMPLETED: { label: 'Completed', color: '#10b981' },
    CANCELLED: { label: 'Cancelled', color: '#ef4444' },
};

// ── Invoice statuses ────────────────────────────────────
export const INVOICE_STATUS = {
    DRAFT: { label: 'Draft', color: '#64748b' },
    SENT: { label: 'Sent', color: '#3b82f6' },
    PAID: { label: 'Paid', color: '#10b981' },
    OVERDUE: { label: 'Overdue', color: '#ef4444' },
    CANCELLED: { label: 'Cancelled', color: '#6b7280' },
};

// ── Estimate statuses ───────────────────────────────────
export const ESTIMATE_STATUS = {
    PENDING: { label: 'Pending', color: '#f59e0b' },
    APPROVED: { label: 'Approved', color: '#10b981' },
    REJECTED: { label: 'Rejected', color: '#ef4444' },
};

// ── SLA types ───────────────────────────────────────────
export const SLA_TYPES = ['DIAGNOSIS', 'REPAIR', 'PARTS_DELIVERY', 'QA'];

// ── Device types ────────────────────────────────────────
export const DEVICE_TYPES = ['LAPTOP', 'DESKTOP', 'DRONE', 'TABLET', 'PHONE'];

// ── Priority levels ─────────────────────────────────────
export const PRIORITIES = {
    LOW: { label: 'Low', color: '#10b981' },
    MEDIUM: { label: 'Medium', color: '#f59e0b' },
    HIGH: { label: 'High', color: '#f97316' },
    CRITICAL: { label: 'Critical', color: '#ef4444' },
};

// ── Navigation sections ────────────────────────────────
export const NAV_SECTIONS = [
    {
        title: 'Operations',
        items: [
            { key: 'dashboard', label: 'Dashboard', path: '/', icon: 'LayoutDashboard' },
            { key: 'tickets', label: 'Tickets', path: '/tickets', icon: 'Ticket' },
            { key: 'customers', label: 'Customers', path: '/customers', icon: 'Users' },
            { key: 'branches', label: 'Branches', path: '/branches', icon: 'Building2' },
            { key: 'technicians', label: 'Technicians', path: '/technicians', icon: 'Wrench' },
        ],
    },
    {
        title: 'Finance',
        items: [
            { key: 'billing', label: 'Invoices', path: '/invoices', icon: 'Receipt' },
            { key: 'billing', label: 'Estimates', path: '/estimates', icon: 'FileText' },
        ],
    },
    {
        title: 'Technical',
        items: [
            { key: 'diagnostics', label: 'Diagnostics', path: '/diagnostics', icon: 'Stethoscope' },
            { key: 'inventory', label: 'Inventory', path: '/inventory', icon: 'Package' },
        ],
    },
    {
        title: 'Management',
        items: [
            { key: 'franchises', label: 'Franchises', path: '/franchises', icon: 'Store' },
            { key: 'sla', label: 'SLA Monitor', path: '/sla', icon: 'ShieldCheck' },
            { key: 'notifications', label: 'Notifications', path: '/notifications', icon: 'Bell' },
        ],
    },
    {
        title: 'Settings',
        items: [
            { key: 'simulation', label: 'Simulation', path: '/simulation', icon: 'FlaskConical' },
            { key: 'audit', label: 'Audit Log', path: '/audit', icon: 'ScrollText' },
            { key: 'groups', label: 'Groups & Permissions', path: '/groups', icon: 'Shield' },
            { key: 'users', label: 'Users', path: '/users', icon: 'UserCog' },
        ],
    },
];

// ── Group levels ────────────────────────────────────────
export const GROUP_LEVELS = ['EXECUTIVE', 'MANAGEMENT', 'OPERATIONAL', 'FINANCE', 'LOGISTICS', 'PARTNER', 'EXTERNAL'];
export const GROUP_LEVEL_COLORS = {
    EXECUTIVE: '#ef4444',
    MANAGEMENT: '#8b5cf6',
    OPERATIONAL: '#3b82f6',
    FINANCE: '#10b981',
    LOGISTICS: '#f59e0b',
    PARTNER: '#f97316',
    EXTERNAL: '#64748b',
};

// ── COLUMN_PERMISSIONS, TAB_PERMISSIONS, SECTION_PERMISSIONS ──
// Migrated to src/config/permissions.config.js (single source of truth).
// Import from there: import { COLUMN_PERMISSIONS, TAB_PERMISSIONS, SECTION_PERMISSIONS } from '../config/permissions.config';

