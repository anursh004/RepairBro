export default function StatusBadge({ status, statusMap }) {
    const config = statusMap?.[status] || { label: status, color: '#64748b' };
    return (
        <span
            className="status-badge"
            style={{
                color: config.color,
                background: `${config.color}18`,
                border: `1px solid ${config.color}30`,
            }}
        >
            <span className="dot" />
            {config.label}
        </span>
    );
}
