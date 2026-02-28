import { useAuth } from '../hooks/useAuth';

/**
 * PermissionGate — renders children only if the current user has
 * at least one of the required permissions.
 *
 * Usage:
 *   <PermissionGate requires={['INVOICE_VIEW', 'INVOICE_CREATE']}>
 *     <InvoiceTable />
 *   </PermissionGate>
 *
 *   <PermissionGate requires={['TICKET_ASSIGN']} fallback={<span>No access</span>}>
 *     <AssignButton />
 *   </PermissionGate>
 */
export default function PermissionGate({ requires = [], fallback = null, children }) {
    const { hasPermission } = useAuth();

    if (!requires.length) return children;

    const allowed = requires.some((perm) => hasPermission(perm));
    return allowed ? children : fallback;
}
