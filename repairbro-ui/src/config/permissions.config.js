// ─────────────────────────────────────────────────────────────────
// RepairBro — Central Permission Registry
// Single source of truth for all RBAC UI configuration.
// Mirrors the backend permission codes from V5 migration exactly.
// ─────────────────────────────────────────────────────────────────

// ── Permission Code Constants ───────────────────────────────────
// Every code here maps 1:1 to a row in the `permission` table
// and a @PreAuthorize("hasAuthority('PERM_<code>')") annotation.
export const P = Object.freeze({
  // Ticket module
  TICKET_CREATE:        'TICKET_CREATE',
  TICKET_VIEW:          'TICKET_VIEW',
  TICKET_VIEW_ALL:      'TICKET_VIEW_ALL',
  TICKET_UPDATE_STATUS: 'TICKET_UPDATE_STATUS',
  TICKET_ASSIGN:        'TICKET_ASSIGN',
  TICKET_ADD_DIAGNOSIS: 'TICKET_ADD_DIAGNOSIS',
  TICKET_CANCEL:        'TICKET_CANCEL',

  // Customer module
  CUSTOMER_CREATE: 'CUSTOMER_CREATE',
  CUSTOMER_VIEW:   'CUSTOMER_VIEW',
  CUSTOMER_EDIT:   'CUSTOMER_EDIT',
  CUSTOMER_SEARCH: 'CUSTOMER_SEARCH',

  // Branch module
  BRANCH_CREATE:     'BRANCH_CREATE',
  BRANCH_VIEW:       'BRANCH_VIEW',
  BRANCH_EDIT:       'BRANCH_EDIT',
  BRANCH_DEACTIVATE: 'BRANCH_DEACTIVATE',

  // Billing — Invoices
  INVOICE_CREATE:  'INVOICE_CREATE',
  INVOICE_VIEW:    'INVOICE_VIEW',
  INVOICE_PAYMENT: 'INVOICE_PAYMENT',

  // Billing — Estimates
  ESTIMATE_CREATE:  'ESTIMATE_CREATE',
  ESTIMATE_VIEW:    'ESTIMATE_VIEW',
  ESTIMATE_APPROVE: 'ESTIMATE_APPROVE',
  ESTIMATE_REJECT:  'ESTIMATE_REJECT',

  // Diagnostics module
  DIAG_VIEW_LIBRARY: 'DIAG_VIEW_LIBRARY',
  DIAG_CREATE_FLOW:  'DIAG_CREATE_FLOW',
  DIAG_EVALUATE:     'DIAG_EVALUATE',

  // Inventory module
  PARTS_VIEW:        'PARTS_VIEW',
  PARTS_CREATE:      'PARTS_CREATE',
  INVENTORY_VIEW:    'INVENTORY_VIEW',
  INVENTORY_RESERVE: 'INVENTORY_RESERVE',
  PROCUREMENT_ORDER: 'PROCUREMENT_ORDER',

  // Franchise module
  FRANCHISE_ONBOARD:  'FRANCHISE_ONBOARD',
  FRANCHISE_VIEW:     'FRANCHISE_VIEW',
  ROYALTY_VIEW:       'ROYALTY_VIEW',
  ROYALTY_CALCULATE:  'ROYALTY_CALCULATE',

  // SLA module
  SLA_VIEW:       'SLA_VIEW',
  SLA_CREATE:     'SLA_CREATE',
  COMPLAINT_VIEW: 'COMPLAINT_VIEW',
  COMPLAINT_FILE: 'COMPLAINT_FILE',

  // Notification module
  NOTIFICATION_VIEW: 'NOTIFICATION_VIEW',
  NOTIFICATION_SEND: 'NOTIFICATION_SEND',

  // Simulation module
  SIMULATION_VIEW:   'SIMULATION_VIEW',
  SIMULATION_CREATE: 'SIMULATION_CREATE',
  SIMULATION_RUN:    'SIMULATION_RUN',

  // Metrics / Reporting
  METRICS_VIEW: 'METRICS_VIEW',
  REPORT_VIEW:  'REPORT_VIEW',

  // User / Admin module
  USER_VIEW_ALL:        'USER_VIEW_ALL',
  USER_CREATE:          'USER_CREATE',
  USER_EDIT:            'USER_EDIT',
  USER_DEACTIVATE:      'USER_DEACTIVATE',
  GROUP_VIEW:           'GROUP_VIEW',
  GROUP_CREATE:         'GROUP_CREATE',
  GROUP_EDIT:           'GROUP_EDIT',
  GROUP_DELETE:         'GROUP_DELETE',
  GROUP_ASSIGN_USER:    'GROUP_ASSIGN_USER',
  USER_ASSIGN_LOCATION: 'USER_ASSIGN_LOCATION',

  // Technician module
  TECHNICIAN_VIEW:   'TECHNICIAN_VIEW',
  TECHNICIAN_CREATE: 'TECHNICIAN_CREATE',
  TECHNICIAN_EDIT:   'TECHNICIAN_EDIT',
});


// ── Section Permissions (sidebar nav gating) ────────────────────
// User needs ANY ONE of the listed permissions to see the section.
export const SECTION_PERMISSIONS = {
  dashboard:     [P.TICKET_VIEW, P.METRICS_VIEW],
  tickets:       [P.TICKET_VIEW, P.TICKET_VIEW_ALL],
  customers:     [P.CUSTOMER_VIEW, P.CUSTOMER_SEARCH],
  branches:      [P.BRANCH_VIEW],
  technicians:   [P.TECHNICIAN_VIEW],
  invoices:      [P.INVOICE_VIEW, P.ESTIMATE_VIEW],
  diagnostics:   [P.DIAG_VIEW_LIBRARY],
  inventory:     [P.PARTS_VIEW, P.INVENTORY_VIEW],
  franchises:    [P.FRANCHISE_VIEW],
  sla:           [P.SLA_VIEW, P.COMPLAINT_VIEW],
  notifications: [P.NOTIFICATION_VIEW],
  simulation:    [P.SIMULATION_VIEW],
  users:         [P.USER_VIEW_ALL],
  groups:        [P.GROUP_VIEW],
  audit:         [P.USER_VIEW_ALL],
};


// ── Action Permissions per Page ─────────────────────────────────
// Maps page -> action -> required permission codes (user needs ANY ONE).
export const ACTION_PERMISSIONS = {
  tickets: {
    create:          [P.TICKET_CREATE],
    updateStatus:    [P.TICKET_UPDATE_STATUS],
    assign:          [P.TICKET_ASSIGN],
    cancel:          [P.TICKET_CANCEL],
    addDiagnosis:    [P.TICKET_ADD_DIAGNOSIS, P.DIAG_EVALUATE],
    export:          [P.TICKET_VIEW_ALL],
    bulkAssign:      [P.TICKET_ASSIGN],
    bulkUpdateStatus:[P.TICKET_UPDATE_STATUS],
  },
  customers: {
    create: [P.CUSTOMER_CREATE],
    edit:   [P.CUSTOMER_EDIT],
    search: [P.CUSTOMER_SEARCH],
    export: [P.CUSTOMER_SEARCH],
  },
  invoices: {
    create:        [P.INVOICE_CREATE],
    recordPayment: [P.INVOICE_PAYMENT],
    export:        [P.INVOICE_VIEW],
  },
  estimates: {
    create:  [P.ESTIMATE_CREATE],
    approve: [P.ESTIMATE_APPROVE],
    reject:  [P.ESTIMATE_REJECT],
  },
  branches: {
    create:     [P.BRANCH_CREATE],
    edit:       [P.BRANCH_EDIT],
    deactivate: [P.BRANCH_DEACTIVATE],
  },
  technicians: {
    create: [P.TECHNICIAN_CREATE],
    edit:   [P.TECHNICIAN_EDIT],
  },
  inventory: {
    createPart: [P.PARTS_CREATE],
    reserve:    [P.INVENTORY_RESERVE],
    placeOrder: [P.PROCUREMENT_ORDER],
    export:     [P.INVENTORY_VIEW],
  },
  franchises: {
    onboard:          [P.FRANCHISE_ONBOARD],
    calculateRoyalty: [P.ROYALTY_CALCULATE],
  },
  sla: {
    create:        [P.SLA_CREATE],
    fileComplaint: [P.COMPLAINT_FILE],
  },
  diagnostics: {
    createFlow: [P.DIAG_CREATE_FLOW],
    evaluate:   [P.DIAG_EVALUATE],
  },
  notifications: {
    send: [P.NOTIFICATION_SEND],
  },
  simulation: {
    create: [P.SIMULATION_CREATE],
    run:    [P.SIMULATION_RUN],
  },
  users: {
    create:         [P.USER_CREATE],
    edit:           [P.USER_EDIT],
    deactivate:     [P.USER_DEACTIVATE],
    assignGroup:    [P.GROUP_ASSIGN_USER],
    assignLocation: [P.USER_ASSIGN_LOCATION],
  },
  groups: {
    create: [P.GROUP_CREATE],
    edit:   [P.GROUP_EDIT],
    delete: [P.GROUP_DELETE],
  },
};


// ── Tab Permissions (workflow-aligned) ──────────────────────────
// Maps page -> tab key -> required permissions (user needs ANY ONE).
export const TAB_PERMISSIONS = {
  tickets: {
    all:              [P.TICKET_VIEW, P.TICKET_VIEW_ALL],
    myAssigned:       [P.TICKET_VIEW],
    pendingDiagnosis: [P.DIAG_EVALUATE],
    awaitingParts:    [P.INVENTORY_VIEW],
    readyForQA:       [P.TICKET_UPDATE_STATUS],
    completed:        [P.TICKET_VIEW, P.TICKET_VIEW_ALL],
  },
  billing: {
    invoices:       [P.INVOICE_VIEW],
    estimates:      [P.ESTIMATE_VIEW],
    payments:       [P.INVOICE_PAYMENT],
    revenueSummary: [P.METRICS_VIEW],
  },
  inventory: {
    stockLevels:    [P.INVENTORY_VIEW],
    partsCatalog:   [P.PARTS_VIEW],
    procurement:    [P.PROCUREMENT_ORDER],
    lowStockAlerts: [P.INVENTORY_VIEW],
  },
  sla: {
    activeSLAs:  [P.SLA_VIEW],
    breached:    [P.SLA_VIEW],
    complaints:  [P.COMPLAINT_VIEW],
    escalations: [P.SLA_VIEW],
  },
  diagnostics: {
    library:  [P.DIAG_VIEW_LIBRARY],
    evaluate: [P.DIAG_EVALUATE],
  },
  franchises: {
    franchises: [P.FRANCHISE_VIEW],
    royalties:  [P.ROYALTY_VIEW],
  },
  notifications: {
    recent: [P.NOTIFICATION_VIEW],
    failed: [P.NOTIFICATION_VIEW],
  },
  users: {
    userList: [P.USER_VIEW_ALL],
  },
  groups: {
    groups:      [P.GROUP_VIEW],
    permissions: [P.GROUP_VIEW],
  },
};


// ── Column Permissions (hide/show) ──────────────────────────────
// Columns not listed are always shown.
// User needs ANY ONE of the listed permissions to see the column.
export const COLUMN_PERMISSIONS = {
  tickets: {
    priority:   [P.TICKET_UPDATE_STATUS, P.TICKET_ASSIGN],
    assignedTo: [P.TICKET_ASSIGN, P.TICKET_VIEW_ALL],
    actions:    [P.TICKET_UPDATE_STATUS, P.TICKET_ASSIGN, P.TICKET_CANCEL],
  },
  customers: {
    actions: [P.CUSTOMER_EDIT],
  },
  invoices: {
    tax:     [P.INVOICE_CREATE, P.INVOICE_PAYMENT],
    total:   [P.INVOICE_VIEW],
    actions: [P.INVOICE_CREATE, P.INVOICE_PAYMENT],
  },
  technicians: {
    actions: [P.TECHNICIAN_EDIT],
  },
  branches: {
    actions: [P.BRANCH_EDIT, P.BRANCH_DEACTIVATE],
  },
};


// ── Column Masking Rules ────────────────────────────────────────
// Columns that show masked data for users lacking specific permissions.
// If user has ANY of `requiresAny`, they see full data; otherwise masked.
export const COLUMN_MASKING = {
  customers: {
    phone: {
      requiresAny: [P.CUSTOMER_EDIT, P.CUSTOMER_CREATE],
      mask: (value) => {
        if (!value) return '--';
        return '\u2022\u2022\u2022\u2022 ' + value.slice(-4);
      },
    },
    email: {
      requiresAny: [P.CUSTOMER_EDIT, P.CUSTOMER_CREATE],
      mask: (value) => {
        if (!value) return '--';
        const [local, domain] = value.split('@');
        return local.charAt(0) + '\u2022\u2022\u2022@' + domain;
      },
    },
  },
  tickets: {
    customerPhone: {
      requiresAny: [P.CUSTOMER_EDIT, P.CUSTOMER_CREATE],
      mask: (value) => (value ? '\u2022\u2022\u2022\u2022 ' + value.slice(-4) : '--'),
    },
  },
  technicians: {
    hourlyRate: {
      requiresAny: [P.TECHNICIAN_EDIT, P.METRICS_VIEW],
      mask: () => '\u2022\u2022\u2022',
    },
  },
  franchises: {
    royaltyPercent: {
      requiresAny: [P.ROYALTY_VIEW, P.ROYALTY_CALCULATE],
      mask: () => '\u2022\u2022\u2022',
    },
    setupFee: {
      requiresAny: [P.FRANCHISE_ONBOARD],
      mask: () => '\u2022\u2022\u2022',
    },
  },
};


// ── Field-Level Edit Permissions ────────────────────────────────
// Maps page -> field -> { editable: [...perms], visible: [...perms] }
// If `visible` is omitted, the field is always visible.
// If `editable` perms are not met, field renders as read-only.
export const FIELD_PERMISSIONS = {
  tickets: {
    status:        { editable: [P.TICKET_UPDATE_STATUS] },
    priority:      { editable: [P.TICKET_UPDATE_STATUS, P.TICKET_ASSIGN] },
    assignedTo:    { editable: [P.TICKET_ASSIGN] },
    branchId:      { editable: [P.TICKET_CREATE] },
    estimatedCost: { editable: [P.ESTIMATE_CREATE], visible: [P.INVOICE_VIEW, P.ESTIMATE_VIEW] },
  },
  customers: {
    name:  { editable: [P.CUSTOMER_EDIT] },
    phone: { editable: [P.CUSTOMER_EDIT] },
    email: { editable: [P.CUSTOMER_EDIT] },
  },
  invoices: {
    amount: { editable: [P.INVOICE_CREATE] },
    tax:    { editable: [P.INVOICE_CREATE], visible: [P.INVOICE_CREATE, P.INVOICE_PAYMENT] },
    status: { editable: [P.INVOICE_PAYMENT] },
  },
  estimates: {
    laborCost:   { editable: [P.ESTIMATE_CREATE] },
    partsCost:   { editable: [P.ESTIMATE_CREATE] },
    description: { editable: [P.ESTIMATE_CREATE] },
  },
  branches: {
    name:   { editable: [P.BRANCH_EDIT] },
    city:   { editable: [P.BRANCH_EDIT] },
    tier:   { editable: [P.BRANCH_EDIT] },
    active: { editable: [P.BRANCH_DEACTIVATE] },
  },
  technicians: {
    skillLevel:      { editable: [P.TECHNICIAN_EDIT] },
    specializations: { editable: [P.TECHNICIAN_EDIT] },
    hourlyRate:      { editable: [P.TECHNICIAN_EDIT], visible: [P.TECHNICIAN_EDIT, P.METRICS_VIEW] },
  },
  inventory: {
    costPrice:    { editable: [P.PARTS_CREATE], visible: [P.PARTS_CREATE, P.PROCUREMENT_ORDER] },
    retailPrice:  { editable: [P.PARTS_CREATE] },
    reorderLevel: { editable: [P.PROCUREMENT_ORDER] },
  },
};


// ── Group Level Priority (for dashboard config resolution) ──────
export const GROUP_LEVEL_PRIORITY = [
  'EXECUTIVE',
  'MANAGEMENT',
  'FINANCE',
  'LOGISTICS',
  'OPERATIONAL',
  'PARTNER',
  'EXTERNAL',
];

// ── Group Name → Level Mapping ──────────────────────────────────
// Mirrors the 12 groups seeded in V5 migration.
export const GROUP_LEVEL_MAP = {
  'Super Admin':             'EXECUTIVE',
  'Regional Manager':        'MANAGEMENT',
  'Service Manager':         'MANAGEMENT',
  'Service Senior Executive':'OPERATIONAL',
  'Service Executive':       'OPERATIONAL',
  'Service Intern':          'OPERATIONAL',
  'Store Counter Executive': 'OPERATIONAL',
  'Billing Admin':           'FINANCE',
  'Billing Viewer':          'FINANCE',
  'Inventory Manager':       'LOGISTICS',
  'Franchise Owner':         'PARTNER',
  'Customer':                'EXTERNAL',
};


// ── Dashboard Configuration per Group Level ─────────────────────
export const DASHBOARD_CONFIG = {
  EXECUTIVE: {
    kpis:   ['totalTickets', 'activeRepairs', 'revenueToday', 'slaScore', 'techniciansOnline', 'lowStockAlerts', 'monthlyRevenue', 'branchCount'],
    charts: ['revenueTrend', 'ticketPipeline', 'branchComparison', 'slaOverview'],
    tables: ['recentTickets', 'topTechnicians'],
  },
  MANAGEMENT: {
    kpis:   ['totalTickets', 'activeRepairs', 'revenueToday', 'slaScore', 'techniciansOnline', 'lowStockAlerts'],
    charts: ['revenueTrend', 'ticketPipeline'],
    tables: ['recentTickets'],
  },
  OPERATIONAL: {
    kpis:   ['myTickets', 'pendingDiagnosis', 'awaitingParts', 'completedToday'],
    charts: ['ticketPipeline'],
    tables: ['myAssignedTickets'],
  },
  FINANCE: {
    kpis:   ['revenueToday', 'pendingInvoices', 'overdueInvoices', 'monthlyRevenue'],
    charts: ['revenueTrend', 'paymentStatus'],
    tables: ['recentInvoices'],
  },
  LOGISTICS: {
    kpis:   ['lowStockAlerts', 'pendingOrders', 'totalParts', 'reservedParts'],
    charts: ['stockLevels'],
    tables: ['lowStockItems'],
  },
  PARTNER: {
    kpis:   ['myTickets', 'myRevenue', 'slaScore', 'royaltyDue'],
    charts: ['revenueTrend'],
    tables: ['recentTickets'],
  },
  EXTERNAL: {
    kpis:   ['myTickets', 'activeRepair'],
    charts: [],
    tables: ['myTickets'],
  },
};
