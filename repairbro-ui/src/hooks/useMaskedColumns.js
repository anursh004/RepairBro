import { useMemo } from 'react';
import { useAuth } from './useAuth';
import { COLUMN_PERMISSIONS, COLUMN_MASKING } from '../config/permissions.config';

/**
 * Enhanced column filtering hook that supports both hiding and masking.
 *
 * - Columns listed in COLUMN_PERMISSIONS are hidden if the user lacks permission.
 * - Columns listed in COLUMN_MASKING show masked values if the user lacks permission,
 *   but the column itself remains visible (e.g., phone => "•••• 4321").
 *
 * Usage:
 *   const columns = useMaskedColumns('customers', allColumns);
 *
 * Column objects must have a `key` property. Optionally a `permKey` override.
 * Columns with a `render` function will have it wrapped to apply masking.
 */
export function useMaskedColumns(page, allColumns) {
  const { hasPermission } = useAuth();

  return useMemo(() => {
    const hideRules = COLUMN_PERMISSIONS[page] || {};
    const maskRules = COLUMN_MASKING[page] || {};

    return allColumns
      .filter((col) => {
        // Step 1: Hide columns the user cannot see at all
        const colKey = col.permKey || col.key;
        const required = hideRules[colKey];
        if (!required) return true;
        return required.some((p) => hasPermission(p));
      })
      .map((col) => {
        // Step 2: Apply masking to columns the user can see but not fully
        const colKey = col.permKey || col.key;
        const maskRule = maskRules[colKey];
        if (!maskRule) return col;

        const canSeeUnmasked = maskRule.requiresAny.some((p) => hasPermission(p));
        if (canSeeUnmasked) return col;

        // Wrap render function to apply masking
        const originalRender = col.render;
        return {
          ...col,
          render: (value, row) => {
            const maskedValue = maskRule.mask(value);
            if (originalRender) return originalRender(maskedValue, row);
            return maskedValue;
          },
          // Mark column as masked for potential UI indication
          _masked: true,
        };
      });
  }, [page, allColumns, hasPermission]);
}
