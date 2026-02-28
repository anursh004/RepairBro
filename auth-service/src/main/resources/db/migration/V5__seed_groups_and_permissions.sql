-- V5__seed_groups_and_permissions.sql
-- Seed all permissions, 12 default groups, and migrate existing users

-- ═══════════════════════════════════════════════════════
-- PERMISSIONS (~60)
-- ═══════════════════════════════════════════════════════

-- ── Ticket Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-0001-4000-a000-000000000001', 'TICKET_CREATE',        'Create Ticket',          'TICKET',  'POST', '/api/v1/tickets',             'Create a new repair ticket'),
('00000000-0001-4000-a000-000000000002', 'TICKET_VIEW',          'View Ticket',            'TICKET',  'GET',  '/api/v1/tickets/{id}',        'View ticket details'),
('00000000-0001-4000-a000-000000000003', 'TICKET_VIEW_ALL',      'View All Tickets',       'TICKET',  'GET',  '/api/v1/tickets/**',          'View all tickets across branches'),
('00000000-0001-4000-a000-000000000004', 'TICKET_UPDATE_STATUS', 'Update Ticket Status',   'TICKET',  'POST', '/api/v1/tickets/*/status',    'Change ticket status'),
('00000000-0001-4000-a000-000000000005', 'TICKET_ASSIGN',        'Assign Technician',      'TICKET',  'POST', '/api/v1/tickets/*/assign/*',  'Assign technician to ticket'),
('00000000-0001-4000-a000-000000000006', 'TICKET_ADD_DIAGNOSIS', 'Add Diagnosis',          'TICKET',  'POST', '/api/v1/tickets/*/diagnose',  'Add diagnosis step to ticket'),
('00000000-0001-4000-a000-000000000007', 'TICKET_CANCEL',        'Cancel Ticket',          'TICKET',  'POST', '/api/v1/tickets/*/cancel',    'Cancel a repair ticket');

-- ── Customer Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-0002-4000-a000-000000000001', 'CUSTOMER_CREATE',  'Create Customer',  'CUSTOMER', 'POST', '/api/v1/customers',      'Create a new customer'),
('00000000-0002-4000-a000-000000000002', 'CUSTOMER_VIEW',    'View Customer',    'CUSTOMER', 'GET',  '/api/v1/customers/{id}', 'View customer details'),
('00000000-0002-4000-a000-000000000003', 'CUSTOMER_EDIT',    'Edit Customer',    'CUSTOMER', 'PUT',  '/api/v1/customers/{id}', 'Edit customer information'),
('00000000-0002-4000-a000-000000000004', 'CUSTOMER_SEARCH',  'Search Customers', 'CUSTOMER', 'GET',  '/api/v1/customers/**',   'Search customers');

-- ── Branch Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-0003-4000-a000-000000000001', 'BRANCH_CREATE',     'Create Branch',     'BRANCH', 'POST',   '/api/v1/branches',      'Create a new branch'),
('00000000-0003-4000-a000-000000000002', 'BRANCH_VIEW',       'View Branch',       'BRANCH', 'GET',    '/api/v1/branches/**',   'View branch details'),
('00000000-0003-4000-a000-000000000003', 'BRANCH_EDIT',       'Edit Branch',       'BRANCH', 'PUT',    '/api/v1/branches/{id}', 'Edit branch information'),
('00000000-0003-4000-a000-000000000004', 'BRANCH_DEACTIVATE', 'Deactivate Branch', 'BRANCH', 'DELETE', '/api/v1/branches/{id}', 'Deactivate a branch');

-- ── Invoice Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-0004-4000-a000-000000000001', 'INVOICE_CREATE',  'Create Invoice',  'BILLING', 'POST', '/api/v1/invoices',            'Create a new invoice'),
('00000000-0004-4000-a000-000000000002', 'INVOICE_VIEW',    'View Invoice',    'BILLING', 'GET',  '/api/v1/invoices/**',         'View invoice details'),
('00000000-0004-4000-a000-000000000003', 'INVOICE_PAYMENT', 'Record Payment',  'BILLING', 'POST', '/api/v1/invoices/*/payments', 'Record payment on invoice');

-- ── Estimate Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-0005-4000-a000-000000000001', 'ESTIMATE_CREATE',  'Create Estimate',  'BILLING', 'POST', '/api/v1/estimates',           'Create a repair estimate'),
('00000000-0005-4000-a000-000000000002', 'ESTIMATE_VIEW',    'View Estimate',    'BILLING', 'GET',  '/api/v1/estimates/**',        'View estimate details'),
('00000000-0005-4000-a000-000000000003', 'ESTIMATE_APPROVE', 'Approve Estimate', 'BILLING', 'POST', '/api/v1/estimates/*/approve', 'Approve a repair estimate'),
('00000000-0005-4000-a000-000000000004', 'ESTIMATE_REJECT',  'Reject Estimate',  'BILLING', 'POST', '/api/v1/estimates/*/reject',  'Reject a repair estimate');

-- ── Diagnostics Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-0006-4000-a000-000000000001', 'DIAG_VIEW_LIBRARY', 'View Diagnostic Library', 'DIAGNOSTICS', 'GET',  '/api/v1/diagnostics/**',      'View diagnostic flows'),
('00000000-0006-4000-a000-000000000002', 'DIAG_CREATE_FLOW',  'Create Diagnostic Flow',  'DIAGNOSTICS', 'POST', '/api/v1/diagnostics',         'Create new diagnostic flow'),
('00000000-0006-4000-a000-000000000003', 'DIAG_EVALUATE',     'Evaluate Diagnosis',      'DIAGNOSTICS', 'POST', '/api/v1/diagnostics/evaluate', 'Run diagnostic evaluation');

-- ── Inventory Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-0007-4000-a000-000000000001', 'PARTS_VIEW',         'View Parts',          'INVENTORY', 'GET',  '/api/v1/parts',              'View spare parts catalog'),
('00000000-0007-4000-a000-000000000002', 'PARTS_CREATE',       'Create Part',         'INVENTORY', 'POST', '/api/v1/parts',              'Add new spare part'),
('00000000-0007-4000-a000-000000000003', 'INVENTORY_VIEW',     'View Inventory',      'INVENTORY', 'GET',  '/api/v1/inventory/**',       'View branch inventory'),
('00000000-0007-4000-a000-000000000004', 'INVENTORY_RESERVE',  'Reserve Parts',       'INVENTORY', 'POST', '/api/v1/inventory/reserve',  'Reserve parts for repair'),
('00000000-0007-4000-a000-000000000005', 'PROCUREMENT_ORDER',  'Place Order',         'INVENTORY', 'POST', '/api/v1/procurement/order',  'Place procurement order');

-- ── Franchise Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-0008-4000-a000-000000000001', 'FRANCHISE_ONBOARD', 'Onboard Franchise',      'FRANCHISE', 'POST', '/api/v1/franchises',            'Onboard a new franchise'),
('00000000-0008-4000-a000-000000000002', 'FRANCHISE_VIEW',    'View Franchise',         'FRANCHISE', 'GET',  '/api/v1/franchises/**',         'View franchise details'),
('00000000-0008-4000-a000-000000000003', 'ROYALTY_VIEW',      'View Royalties',         'FRANCHISE', 'GET',  '/api/v1/royalties/**',          'View royalty records'),
('00000000-0008-4000-a000-000000000004', 'ROYALTY_CALCULATE', 'Calculate Royalty',      'FRANCHISE', 'POST', '/api/v1/royalties/calculate',   'Calculate royalty');

-- ── SLA Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-0009-4000-a000-000000000001', 'SLA_VIEW',       'View SLA',       'SLA', 'GET',  '/api/v1/sla/**',        'View SLA records'),
('00000000-0009-4000-a000-000000000002', 'SLA_CREATE',     'Create SLA',     'SLA', 'POST', '/api/v1/sla',           'Create SLA record'),
('00000000-0009-4000-a000-000000000003', 'COMPLAINT_VIEW', 'View Complaints','SLA', 'GET',  '/api/v1/complaints/**', 'View complaints'),
('00000000-0009-4000-a000-000000000004', 'COMPLAINT_FILE', 'File Complaint', 'SLA', 'POST', '/api/v1/complaints',    'File a new complaint');

-- ── Notification Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-000a-4000-a000-000000000001', 'NOTIFICATION_VIEW', 'View Notifications',  'NOTIFICATION', 'GET',  '/api/v1/notifications/**', 'View notification logs'),
('00000000-000a-4000-a000-000000000002', 'NOTIFICATION_SEND', 'Send Notification',   'NOTIFICATION', 'POST', '/api/v1/notifications/**', 'Send manual notification');

-- ── Simulation Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-000b-4000-a000-000000000001', 'SIMULATION_VIEW',   'View Scenarios',  'SIMULATION', 'GET',  '/api/v1/simulation/**',      'View simulation scenarios'),
('00000000-000b-4000-a000-000000000002', 'SIMULATION_CREATE', 'Create Scenario', 'SIMULATION', 'POST', '/api/v1/simulation/scenarios','Create simulation scenario'),
('00000000-000b-4000-a000-000000000003', 'SIMULATION_RUN',    'Run Simulation',  'SIMULATION', 'POST', '/api/v1/simulation/*/run',   'Execute a simulation');

-- ── Metrics Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-000c-4000-a000-000000000001', 'METRICS_VIEW', 'View Metrics',  'METRICS', 'GET', '/api/v1/metrics/**',  'View branch metrics'),
('00000000-000c-4000-a000-000000000002', 'REPORT_VIEW',  'View Reports',  'METRICS', 'GET', '/api/v1/reports/**',  'View daily reports');

-- ── User/Group Administration ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-000d-4000-a000-000000000001', 'USER_VIEW_ALL',          'View All Users',     'ADMIN', 'GET',    '/api/v1/users',                   'View all users'),
('00000000-000d-4000-a000-000000000002', 'USER_CREATE',            'Create User',        'ADMIN', 'POST',   '/api/v1/auth/register',           'Register new user'),
('00000000-000d-4000-a000-000000000003', 'USER_EDIT',              'Edit User',          'ADMIN', 'PUT',    '/api/v1/users/{id}',              'Edit user info'),
('00000000-000d-4000-a000-000000000004', 'USER_DEACTIVATE',        'Deactivate User',    'ADMIN', 'DELETE', '/api/v1/users/{id}',              'Deactivate a user'),
('00000000-000d-4000-a000-000000000005', 'GROUP_VIEW',             'View Groups',        'ADMIN', 'GET',    '/api/v1/groups/**',               'View permission groups'),
('00000000-000d-4000-a000-000000000006', 'GROUP_CREATE',           'Create Group',       'ADMIN', 'POST',   '/api/v1/groups',                  'Create permission group'),
('00000000-000d-4000-a000-000000000007', 'GROUP_EDIT',             'Edit Group',         'ADMIN', 'PUT',    '/api/v1/groups/{id}',             'Edit permission group'),
('00000000-000d-4000-a000-000000000008', 'GROUP_DELETE',           'Delete Group',       'ADMIN', 'DELETE', '/api/v1/groups/{id}',             'Delete permission group'),
('00000000-000d-4000-a000-000000000009', 'GROUP_ASSIGN_USER',      'Assign Group',       'ADMIN', 'POST',  '/api/v1/users/*/groups',          'Assign user to group'),
('00000000-000d-4000-a000-00000000000a', 'USER_ASSIGN_LOCATION',   'Assign Location',    'ADMIN', 'POST',  '/api/v1/users/*/locations',       'Assign location to user');

-- ── Technician Module ──
INSERT INTO permission (id, code, name, module, http_method, api_pattern, description) VALUES
('00000000-000e-4000-a000-000000000001', 'TECHNICIAN_VIEW',   'View Technicians',   'TECHNICIAN', 'GET',  '/api/v1/technicians/**', 'View technician profiles'),
('00000000-000e-4000-a000-000000000002', 'TECHNICIAN_CREATE', 'Create Technician',  'TECHNICIAN', 'POST', '/api/v1/technicians',    'Create technician profile'),
('00000000-000e-4000-a000-000000000003', 'TECHNICIAN_EDIT',   'Edit Technician',    'TECHNICIAN', 'PUT',  '/api/v1/technicians/*',  'Edit technician profile');


-- ═══════════════════════════════════════════════════════
-- PERMISSION GROUPS (12 default groups)
-- ═══════════════════════════════════════════════════════

INSERT INTO permission_group (id, name, description, level, system_defined) VALUES
('10000000-0001-4000-b000-000000000001', 'Super Admin',             'Full system access — all modules and user management',        'EXECUTIVE',   TRUE),
('10000000-0001-4000-b000-000000000002', 'Regional Manager',        'Multi-branch oversight with SLA, reports, and simulation',    'MANAGEMENT',  TRUE),
('10000000-0001-4000-b000-000000000003', 'Service Manager',         'Full operations for a single branch',                         'MANAGEMENT',  TRUE),
('10000000-0001-4000-b000-000000000004', 'Service Senior Executive','Senior technician with billing and diagnosis authority',       'OPERATIONAL', TRUE),
('10000000-0001-4000-b000-000000000005', 'Service Executive',       'Standard technician — repairs and diagnostics',               'OPERATIONAL', TRUE),
('10000000-0001-4000-b000-000000000006', 'Service Intern',          'Trainee — view-only for most modules',                        'OPERATIONAL', TRUE),
('10000000-0001-4000-b000-000000000007', 'Store Counter Executive', 'Customer intake, ticket creation, estimates',                 'OPERATIONAL', TRUE),
('10000000-0001-4000-b000-000000000008', 'Billing Admin',           'Full invoice and estimate management',                        'FINANCE',     TRUE),
('10000000-0001-4000-b000-000000000009', 'Billing Viewer',          'Read-only access to billing data',                            'FINANCE',     TRUE),
('10000000-0001-4000-b000-00000000000a', 'Inventory Manager',       'Parts catalog, stock, and procurement management',            'LOGISTICS',   TRUE),
('10000000-0001-4000-b000-00000000000b', 'Franchise Owner',         'Own franchise data and royalty viewing',                       'PARTNER',     TRUE),
('10000000-0001-4000-b000-00000000000c', 'Customer',                'View own tickets only',                                       'EXTERNAL',    TRUE);


-- ═══════════════════════════════════════════════════════
-- GROUP ↔ PERMISSION ASSIGNMENTS
-- ═══════════════════════════════════════════════════════

-- ── 1. Super Admin → ALL permissions ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-000000000001', id FROM permission;

-- ── 2. Regional Manager ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-000000000002', id FROM permission WHERE code IN (
  'TICKET_CREATE','TICKET_VIEW','TICKET_VIEW_ALL','TICKET_UPDATE_STATUS','TICKET_ASSIGN','TICKET_CANCEL',
  'CUSTOMER_CREATE','CUSTOMER_VIEW','CUSTOMER_EDIT','CUSTOMER_SEARCH',
  'BRANCH_VIEW','BRANCH_EDIT',
  'INVOICE_VIEW','ESTIMATE_VIEW',
  'DIAG_VIEW_LIBRARY','DIAG_EVALUATE',
  'PARTS_VIEW','INVENTORY_VIEW',
  'SLA_VIEW','SLA_CREATE','COMPLAINT_VIEW','COMPLAINT_FILE',
  'NOTIFICATION_VIEW','NOTIFICATION_SEND',
  'SIMULATION_VIEW','SIMULATION_RUN',
  'METRICS_VIEW','REPORT_VIEW',
  'TECHNICIAN_VIEW'
);

-- ── 3. Service Manager ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-000000000003', id FROM permission WHERE code IN (
  'TICKET_CREATE','TICKET_VIEW','TICKET_UPDATE_STATUS','TICKET_ASSIGN','TICKET_ADD_DIAGNOSIS','TICKET_CANCEL',
  'CUSTOMER_CREATE','CUSTOMER_VIEW','CUSTOMER_EDIT','CUSTOMER_SEARCH',
  'BRANCH_VIEW',
  'INVOICE_CREATE','INVOICE_VIEW','INVOICE_PAYMENT',
  'ESTIMATE_CREATE','ESTIMATE_VIEW','ESTIMATE_APPROVE','ESTIMATE_REJECT',
  'DIAG_VIEW_LIBRARY','DIAG_CREATE_FLOW','DIAG_EVALUATE',
  'PARTS_VIEW','INVENTORY_VIEW','INVENTORY_RESERVE','PROCUREMENT_ORDER',
  'SLA_VIEW','COMPLAINT_VIEW','COMPLAINT_FILE',
  'NOTIFICATION_VIEW','NOTIFICATION_SEND',
  'METRICS_VIEW','REPORT_VIEW',
  'TECHNICIAN_VIEW','TECHNICIAN_CREATE','TECHNICIAN_EDIT'
);

-- ── 4. Service Senior Executive ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-000000000004', id FROM permission WHERE code IN (
  'TICKET_CREATE','TICKET_VIEW','TICKET_UPDATE_STATUS','TICKET_ADD_DIAGNOSIS',
  'CUSTOMER_VIEW','CUSTOMER_SEARCH',
  'INVOICE_VIEW',
  'ESTIMATE_CREATE','ESTIMATE_VIEW',
  'DIAG_VIEW_LIBRARY','DIAG_EVALUATE',
  'PARTS_VIEW','INVENTORY_VIEW',
  'METRICS_VIEW'
);

-- ── 5. Service Executive ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-000000000005', id FROM permission WHERE code IN (
  'TICKET_CREATE','TICKET_VIEW','TICKET_UPDATE_STATUS','TICKET_ADD_DIAGNOSIS',
  'CUSTOMER_VIEW','CUSTOMER_SEARCH',
  'DIAG_VIEW_LIBRARY','DIAG_EVALUATE',
  'PARTS_VIEW','INVENTORY_VIEW'
);

-- ── 6. Service Intern ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-000000000006', id FROM permission WHERE code IN (
  'TICKET_VIEW',
  'CUSTOMER_VIEW',
  'DIAG_VIEW_LIBRARY',
  'PARTS_VIEW','INVENTORY_VIEW'
);

-- ── 7. Store/Counter Executive ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-000000000007', id FROM permission WHERE code IN (
  'TICKET_CREATE','TICKET_VIEW',
  'CUSTOMER_CREATE','CUSTOMER_VIEW','CUSTOMER_SEARCH',
  'INVOICE_CREATE','INVOICE_VIEW','INVOICE_PAYMENT',
  'ESTIMATE_VIEW'
);

-- ── 8. Billing Admin ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-000000000008', id FROM permission WHERE code IN (
  'INVOICE_CREATE','INVOICE_VIEW','INVOICE_PAYMENT',
  'ESTIMATE_CREATE','ESTIMATE_VIEW','ESTIMATE_APPROVE','ESTIMATE_REJECT',
  'TICKET_VIEW'
);

-- ── 9. Billing Viewer ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-000000000009', id FROM permission WHERE code IN (
  'INVOICE_VIEW','ESTIMATE_VIEW','TICKET_VIEW'
);

-- ── 10. Inventory Manager ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-00000000000a', id FROM permission WHERE code IN (
  'PARTS_VIEW','PARTS_CREATE',
  'INVENTORY_VIEW','INVENTORY_RESERVE','PROCUREMENT_ORDER',
  'TICKET_VIEW'
);

-- ── 11. Franchise Owner ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-00000000000b', id FROM permission WHERE code IN (
  'FRANCHISE_VIEW','ROYALTY_VIEW',
  'TICKET_VIEW','INVOICE_VIEW','ESTIMATE_VIEW',
  'SLA_VIEW','COMPLAINT_VIEW','COMPLAINT_FILE',
  'METRICS_VIEW'
);

-- ── 12. Customer ──
INSERT INTO group_permissions (group_id, permission_id)
SELECT '10000000-0001-4000-b000-00000000000c', id FROM permission WHERE code IN (
  'TICKET_VIEW'
);


-- ═══════════════════════════════════════════════════════
-- MIGRATE EXISTING USERS: user_roles → user_groups
-- ═══════════════════════════════════════════════════════

-- ADMIN → Super Admin
INSERT INTO user_groups (user_id, group_id)
SELECT ur.user_id, '10000000-0001-4000-b000-000000000001'
FROM user_roles ur WHERE ur.role = 'ADMIN'
ON CONFLICT DO NOTHING;

-- BRANCH_MANAGER → Service Manager
INSERT INTO user_groups (user_id, group_id)
SELECT ur.user_id, '10000000-0001-4000-b000-000000000003'
FROM user_roles ur WHERE ur.role = 'BRANCH_MANAGER'
ON CONFLICT DO NOTHING;

-- TECH → Service Executive
INSERT INTO user_groups (user_id, group_id)
SELECT ur.user_id, '10000000-0001-4000-b000-000000000005'
FROM user_roles ur WHERE ur.role = 'TECH'
ON CONFLICT DO NOTHING;

-- FRANCHISEE → Franchise Owner
INSERT INTO user_groups (user_id, group_id)
SELECT ur.user_id, '10000000-0001-4000-b000-00000000000b'
FROM user_roles ur WHERE ur.role = 'FRANCHISEE'
ON CONFLICT DO NOTHING;

-- CUSTOMER → Customer
INSERT INTO user_groups (user_id, group_id)
SELECT ur.user_id, '10000000-0001-4000-b000-00000000000c'
FROM user_roles ur WHERE ur.role = 'CUSTOMER'
ON CONFLICT DO NOTHING;

-- ═══════════════════════════════════════════════════════
-- MIGRATE EXISTING USERS: users.branch_id → user_locations
-- ═══════════════════════════════════════════════════════

INSERT INTO user_locations (user_id, branch_id, primary_location)
SELECT id, branch_id, TRUE
FROM users
WHERE branch_id IS NOT NULL
ON CONFLICT DO NOTHING;
