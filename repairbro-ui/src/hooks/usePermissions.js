import { useMemo } from 'react';
import { useAuth } from './useAuth';
import { COLUMN_PERMISSIONS, TAB_PERMISSIONS } from '../utils/constants';

/**
 * Filter table columns based on user permissions.
 *
 * Usage:
 *   const allColumns = [ { key: 'id', ... }, { key: 'actions', ... } ];
 *   const columns = usePermittedColumns('tickets', allColumns);
 *
 * Any column whose `key` is listed in COLUMN_PERMISSIONS[page]
 * is only shown if the user has at least one of the required permissions.
 * Columns NOT listed in the map are always shown.
 */
export function usePermittedColumns(page, allColumns) {
    const { hasPermission } = useAuth();

    return useMemo(() => {
        const rules = COLUMN_PERMISSIONS[page];
        if (!rules) return allColumns;

        return allColumns.filter((col) => {
            const colKey = col.permKey || col.key;
            const required = rules[colKey];
            if (!required) return true;  // no restriction → always show
            return required.some((p) => hasPermission(p));
        });
    }, [page, allColumns, hasPermission]);
}

/**
 * Get permitted tabs for a given page.
 *
 * Usage:
 *   const tabs = usePermittedTabs('billing');
 *   // returns ['invoices', 'estimates'] or just ['invoices'] etc.
 */
export function usePermittedTabs(page) {
    const { hasPermission } = useAuth();

    return useMemo(() => {
        const rules = TAB_PERMISSIONS[page];
        if (!rules) return null; // no rules → all tabs visible

        return Object.entries(rules)
            .filter(([, perms]) => perms.some((p) => hasPermission(p)))
            .map(([tabKey]) => tabKey);
    }, [page, hasPermission]);
}
