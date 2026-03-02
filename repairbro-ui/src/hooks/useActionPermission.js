import { useMemo } from 'react';
import { useAuth } from './useAuth';
import { ACTION_PERMISSIONS } from '../config/permissions.config';

/**
 * Returns action permission checks for a given page.
 *
 * Usage:
 *   const { canCreate, canEdit, canExport, can } = useActionPermission('tickets');
 *   {canCreate && <button>+ New Ticket</button>}
 *   {can('bulkAssign') && <button>Bulk Assign</button>}
 */
export function useActionPermission(page) {
  const { hasPermission } = useAuth();

  return useMemo(() => {
    const rules = ACTION_PERMISSIONS[page] || {};

    const can = (action) => {
      const perms = rules[action];
      if (!perms) return false;
      return perms.some((p) => hasPermission(p));
    };

    return {
      can,
      canCreate:  can('create'),
      canEdit:    can('edit'),
      canDelete:  can('delete'),
      canExport:  can('export'),
      canApprove: can('approve'),
      canReject:  can('reject'),
      canAssign:  can('assign'),
    };
  }, [page, hasPermission]);
}
