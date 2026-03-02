import { useState, useEffect } from 'react';
import { Shield, Filter, Clock, User, Settings, AlertCircle } from 'lucide-react';
import DataTable from '../../components/DataTable';
import { formatDateTime, shortId } from '../../utils/formatters';
import { auditApi } from '../../api/audit';

const SAMPLE_EVENTS = Array.from({ length: 30 }, (_, i) => ({
    id: `evt-${i}`,
    userId: `user-${i % 6}`,
    userName: ['Admin User', 'Rajesh Manager', 'Vikram Tech', 'Priya Billing', 'Sunil Franchise', 'Deepa Manager'][i % 6],
    action: ['LOGIN', 'TICKET_CREATE', 'TICKET_UPDATE', 'INVOICE_CREATE', 'GROUP_PERMISSION_ADD', 'GROUP_PERMISSION_REMOVE', 'USER_ASSIGN_GROUP', 'TICKET_ASSIGN', 'ESTIMATE_APPROVE', 'BRANCH_DEACTIVATE'][i % 10],
    module: ['AUTH', 'TICKET', 'TICKET', 'BILLING', 'ADMIN', 'ADMIN', 'ADMIN', 'TICKET', 'BILLING', 'BRANCH'][i % 10],
    details: ['User logged in from 192.168.1.1', 'Created ticket #1024 for Dell XPS', 'Updated status to IN_REPAIR', 'Generated invoice INV-0045', 'Added TICKET_VIEW to Service Intern', 'Removed INVOICE_CREATE from Billing Viewer', 'Assigned Vikram to Super Admin group', 'Assigned Priya to ticket #1028', 'Approved estimate EST-0012', 'Deactivated Pune branch'][i % 10],
    ipAddress: ['192.168.1.1', '10.0.0.5', '172.16.0.8', '192.168.2.3', '10.0.1.2', '172.16.1.5'][i % 6],
    createdAt: new Date(Date.now() - i * 3600000 * 2).toISOString(),
}));

const MODULE_COLORS = {
    AUTH: 'var(--accent-purple)',
    TICKET: 'var(--accent-blue)',
    BILLING: 'var(--accent-emerald)',
    ADMIN: 'var(--accent-red)',
    BRANCH: 'var(--accent-amber)',
    INVENTORY: 'var(--accent-cyan)',
};

export default function AuditLogPage() {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [moduleFilter, setModuleFilter] = useState('');
    const [actionFilter, setActionFilter] = useState('');

    useEffect(() => {
        load();
    }, []);

    const load = async () => {
        setLoading(true);
        try {
            const d = await auditApi.getActions();
            setEvents(Array.isArray(d) ? d : []);
        } catch {
            setEvents(SAMPLE_EVENTS);
        } finally {
            setLoading(false);
        }
    };

    const filtered = events.filter(e => {
        if (moduleFilter && e.module !== moduleFilter) return false;
        if (actionFilter && !e.action.toLowerCase().includes(actionFilter.toLowerCase())) return false;
        return true;
    });

    const modules = [...new Set(events.map(e => e.module))];

    return (
        <div className="slide-in">
            <div className="page-header">
                <h1>Audit Log</h1>
                <span className="status-badge" style={{ color: 'var(--accent-purple)', background: 'var(--accent-purple-glow)' }}>
                    <Shield size={14} /> {filtered.length} events
                </span>
            </div>

            <div className="filter-bar">
                <select className="form-select" style={{ width: 180 }} value={moduleFilter} onChange={e => setModuleFilter(e.target.value)}>
                    <option value="">All Modules</option>
                    {modules.map(m => <option key={m} value={m}>{m}</option>)}
                </select>
                <div className="search-input">
                    <Filter size={16} className="search-icon" />
                    <input placeholder="Filter by action..." value={actionFilter} onChange={e => setActionFilter(e.target.value)} />
                </div>
            </div>

            <div className="card">
                {loading ? (
                    <div className="loading-spinner"><div className="spinner" /></div>
                ) : (
                    <DataTable columns={[
                        { key: 'createdAt', label: 'Time', render: v => <span style={{ fontSize: 12, color: 'var(--text-muted)' }}>{formatDateTime(v)}</span> },
                        { key: 'userName', label: 'User', render: v => <b>{v}</b> },
                        {
                            key: 'module', label: 'Module', render: v => (
                                <span className="status-badge" style={{ color: MODULE_COLORS[v] || 'var(--text-muted)', background: `${MODULE_COLORS[v] || 'var(--text-muted)'}18` }}>{v}</span>
                            )
                        },
                        { key: 'action', label: 'Action', render: v => <span style={{ fontFamily: 'monospace', fontSize: 12, color: 'var(--accent-cyan)' }}>{v}</span> },
                        { key: 'details', label: 'Details', render: v => <span className="truncate" style={{ maxWidth: 280, display: 'inline-block' }}>{v}</span> },
                        { key: 'ipAddress', label: 'IP', render: v => <span style={{ fontFamily: 'monospace', fontSize: 12, color: 'var(--text-muted)' }}>{v}</span> },
                    ]} data={filtered} />
                )}
            </div>
        </div>
    );
}
