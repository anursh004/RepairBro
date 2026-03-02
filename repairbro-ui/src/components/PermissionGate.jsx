import { useAuth } from '../hooks/useAuth';
import { ACTION_PERMISSIONS } from '../config/permissions.config';

/**
 * PermissionGate — renders children only if the current user has
 * at least one of the required permissions.
 *
 * Usage (explicit permissions):
 *   <PermissionGate requires={['INVOICE_VIEW', 'INVOICE_CREATE']}>
 *     <InvoiceTable />
 *   </PermissionGate>
 *
 * Usage (page + action shorthand — looks up ACTION_PERMISSIONS):
 *   <PermissionGate page="tickets" action="create">
 *     <button>+ New Ticket</button>
 *   </PermissionGate>
 *
 * Usage (fallback):
 *   <PermissionGate requires={['TICKET_ASSIGN']} fallback={<span>No access</span>}>
 *     <AssignButton />
 *   </PermissionGate>
 */
export default function PermissionGate({
    requires = [],
    page,
    action,
    fallback = null,
    children,
}) {
    const { hasPermission } = useAuth();

    // Resolve permissions from page+action shorthand if provided
    let perms = requires;
    if (page && action && !perms.length) {
        perms = ACTION_PERMISSIONS[page]?.[action] || [];
    }

    if (!perms.length) return children;

    const allowed = perms.some((perm) => hasPermission(perm));
    return allowed ? children : fallback;
}
