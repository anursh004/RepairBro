import { useMemo } from 'react';
import { useAuth } from './useAuth';
import { FIELD_PERMISSIONS } from '../config/permissions.config';

/**
 * Returns field-level permission checks for a given page.
 *
 * Usage:
 *   const { isEditable, isVisible, getFieldProps } = useFieldPermission('tickets');
 *
 *   // Check individually
 *   if (!isVisible('estimatedCost')) return null;
 *
 *   // Spread onto form inputs
 *   <input {...getFieldProps('status')} value={status} onChange={...} />
 *   // => { readOnly: true, disabled: true, className: 'form-input field-readonly' }
 *   //    if user lacks TICKET_UPDATE_STATUS
 */
export function useFieldPermission(page) {
  const { hasPermission } = useAuth();

  return useMemo(() => {
    const rules = FIELD_PERMISSIONS[page] || {};

    const isEditable = (field) => {
      const rule = rules[field];
      if (!rule || !rule.editable) return true; // no rule = editable
      return rule.editable.some((p) => hasPermission(p));
    };

    const isVisible = (field) => {
      const rule = rules[field];
      if (!rule || !rule.visible) return true; // no rule = always visible
      return rule.visible.some((p) => hasPermission(p));
    };

    /**
     * Returns props to spread onto a form input element.
     * If not editable: readOnly, disabled, and a dimmed CSS class.
     */
    const getFieldProps = (field) => {
      const editable = isEditable(field);
      if (editable) {
        return { readOnly: false, disabled: false, className: 'form-input' };
      }
      return {
        readOnly: true,
        disabled: true,
        className: 'form-input field-readonly',
      };
    };

    return { isEditable, isVisible, getFieldProps };
  }, [page, hasPermission]);
}
