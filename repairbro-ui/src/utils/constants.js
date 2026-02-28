// ── Legacy Roles (deprecated — use groups/permissions) ──
export const ROLES = {
    ADMIN: 'ADMIN',
    BRANCH_MANAGER: 'BRANCH_MANAGER',
    TECHNICIAN: 'TECHNICIAN',
    FRANCHISEE: 'FRANCHISEE',
    CUSTOMER: 'CUSTOMER',
};

// ── Permission → Section Mapping ────────────────────────
// Maps a nav section key to the permission(s) needed to see it.
// User needs at least ONE permission in the list to see the section.
export const SECTION_PERMISSIONS = {
    dashboard: ['TICKET_VIEW', 'METRICS_VIEW'],
    tickets: ['TICKET_VIEW', 'TICKET_CREATE'],
    customers: ['CUSTOMER_VIEW', 'CUSTOMER_CREATE'],
    branches: ['BRANCH_VIEW', 'BRANCH_CREATE'],
    technicians: ['TECHNICIAN_VIEW', 'TECHNICIAN_CREATE'],
    billing: ['INVOICE_VIEW', 'ESTIMATE_VIEW'],
    diagnostics: ['DIAG_VIEW_LIBRARY', 'DIAG_EVALUATE'],
    inventory: ['PARTS_VIEW', 'INVENTORY_VIEW'],
    franchises: ['FRANCHISE_VIEW', 'ROYALTY_VIEW'],
    sla: ['SLA_VIEW', 'COMPLAINT_VIEW'],
    notifications: ['NOTIFICATION_VIEW'],
    simulation: ['SIMULATION_VIEW'],
    users: ['USER_VIEW_ALL', 'GROUP_VIEW'],
    groups: ['GROUP_VIEW', 'GROUP_CREATE'],
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

// ── Column-level permissions ───────────────────────────
// Maps page → column key → array of permissions (user needs ANY ONE to see column).
// Columns NOT listed here are always visible.
export const COLUMN_PERMISSIONS = {
    tickets: {
        priority: ['TICKET_UPDATE_STATUS', 'TICKET_ASSIGN'],
        assignedTo: ['TICKET_ASSIGN'],
    },
    invoices: {
        tax: ['INVOICE_CREATE', 'INVOICE_PAYMENT'],
        total: ['INVOICE_CREATE', 'INVOICE_PAYMENT'],
        actions: ['INVOICE_PAYMENT'],
    },
    estimates: {
        laborCost: ['ESTIMATE_CREATE', 'ESTIMATE_APPROVE'],
        partsCost: ['ESTIMATE_CREATE', 'ESTIMATE_APPROVE'],
        total: ['ESTIMATE_CREATE', 'ESTIMATE_APPROVE'],
        actions: ['ESTIMATE_APPROVE', 'ESTIMATE_REJECT'],
    },
    inventory: {
        reserved: ['INVENTORY_RESERVE'],
        reorderLevel: ['PROCUREMENT_ORDER'],
        actions: ['INVENTORY_RESERVE', 'PROCUREMENT_ORDER'],
    },
    branches: {
        actions: ['BRANCH_EDIT', 'BRANCH_DEACTIVATE'],
    },
    customers: {
        actions: ['CUSTOMER_EDIT'],
    },
    technicians: {
        hourlyRate: ['TECHNICIAN_EDIT', 'METRICS_VIEW'],
        actions: ['TECHNICIAN_EDIT'],
    },
    sla: {
        actions: ['SLA_CREATE', 'COMPLAINT_FILE'],
    },
    franchises: {
        royaltyPct: ['ROYALTY_VIEW', 'ROYALTY_CALCULATE'],
        actions: ['FRANCHISE_ONBOARD'],
    },
    users: {
        actions: ['GROUP_ASSIGN_USER', 'USER_ASSIGN_LOCATION'],
    },
};

// ── Tab-level permissions ──────────────────────────────
// Maps page → tab key → required permissions. Tab hidden if user lacks all.
export const TAB_PERMISSIONS = {
    billing: {
        invoices: ['INVOICE_VIEW'],
        estimates: ['ESTIMATE_VIEW'],
    },
    sla: {
        slaRecords: ['SLA_VIEW'],
        complaints: ['COMPLAINT_VIEW'],
    },
    inventory: {
        parts: ['PARTS_VIEW'],
        stock: ['INVENTORY_VIEW'],
        orders: ['PROCUREMENT_ORDER'],
    },
};

