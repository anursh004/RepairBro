import { useFieldPermission } from '../hooks/useFieldPermission';

/**
 * PermissionField — wraps a form field to enforce visibility and editability
 * based on the current user's permissions.
 *
 * Behavior:
 *   - If the field is not visible: renders nothing
 *   - If the field is not editable: renders a styled read-only display
 *   - If the field is editable: renders children as-is
 *
 * Usage:
 *   <PermissionField page="tickets" field="status" value={ticket.status}>
 *     <select value={status} onChange={...}>...</select>
 *   </PermissionField>
 *
 *   // If user lacks TICKET_UPDATE_STATUS, this renders as:
 *   //   <div class="field-readonly-display">IN_REPAIR</div>
 *   // instead of the <select>
 *
 * Props:
 *   - page: string — page key (e.g., 'tickets', 'customers')
 *   - field: string — field key (e.g., 'status', 'phone')
 *   - value: any — current value (displayed when read-only)
 *   - label: string — optional label for the form group
 *   - renderValue: (value) => ReactNode — optional custom read-only renderer
 *   - children: ReactNode — the editable form input
 */
export default function PermissionField({
  page,
  field,
  value,
  label,
  renderValue,
  children,
}) {
  const { isEditable, isVisible } = useFieldPermission(page);

  // Hidden field: render nothing
  if (!isVisible(field)) return null;

  // Editable field: render children as-is
  if (isEditable(field)) {
    return label ? (
      <div className="form-group">
        <label className="form-label">{label}</label>
        {children}
      </div>
    ) : (
      children
    );
  }

  // Read-only field: render a premium styled display
  const displayValue = renderValue
    ? renderValue(value)
    : (value ?? '--');

  return (
    <div className="form-group">
      {label && <label className="form-label">{label}</label>}
      <div className="field-readonly-display">{displayValue}</div>
    </div>
  );
}
