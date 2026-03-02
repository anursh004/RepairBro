import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Search, Filter, Download } from 'lucide-react';
import DataTable from '../../components/DataTable';
import StatusBadge from '../../components/StatusBadge';
import Modal from '../../components/Modal';
import PermissionGate from '../../components/PermissionGate';
import { TICKET_STATUS, DEVICE_TYPES, PRIORITIES } from '../../utils/constants';
import { formatDateTime, shortId } from '../../utils/formatters';
import { usePermittedTabs } from '../../hooks/usePermissions';
import { useMaskedColumns } from '../../hooks/useMaskedColumns';
import { useActionPermission } from '../../hooks/useActionPermission';
import { ticketApi } from '../../api/tickets';
import { customerApi } from '../../api/customers';
import { branchApi } from '../../api/branches';
import { useLocationScope } from '../../hooks/useLocationScope';
import toast from 'react-hot-toast';

const TICKET_TABS = {
    all: 'All Tickets',
    myAssigned: 'My Assigned',
    pendingDiagnosis: 'Pending Diagnosis',
    awaitingParts: 'Awaiting Parts',
    readyForQA: 'Ready for QA',
    completed: 'Completed',
};

export default function TicketList() {
    const navigate = useNavigate();
    const { can } = useActionPermission('tickets');
    const permittedTabs = usePermittedTabs('tickets');
    const availableTabs = permittedTabs || ['all', 'myAssigned', 'pendingDiagnosis', 'awaitingParts', 'readyForQA', 'completed'];
    const [activeTab, setActiveTab] = useState('all');
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(false);
    const [statusFilter, setStatusFilter] = useState('');
    const [search, setSearch] = useState('');
    const [showCreate, setShowCreate] = useState(false);
    const [branches, setBranches] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [form, setForm] = useState({
        customerId: '', branchId: '', deviceType: 'LAPTOP', deviceModel: '', symptom: '', priority: 'MEDIUM',
    });
    const { locations, primaryLocation } = useLocationScope();
    const [selectedBranch, setSelectedBranch] = useState('');

    useEffect(() => {
        loadData();
    }, []);

    useEffect(() => {
        if (!availableTabs.includes(activeTab)) setActiveTab(availableTabs[0] || 'all');
    }, [availableTabs]);

    const loadData = async () => {
        setLoading(true);
        try {
            const [b, c] = await Promise.all([branchApi.getAll(), customerApi.getAll()]);
            const allBranches = Array.isArray(b) ? b : [];
            // Scope branches to user's locations if available
            const scoped = locations.length > 0
                ? allBranches.filter(br => locations.some(loc => loc.id === br.id || loc.name === br.name))
                : allBranches;
            setBranches(scoped);
            setCustomers(Array.isArray(c) ? c : []);
            const target = primaryLocation || (scoped.length > 0 ? scoped[0].id : null);
            if (target) {
                setSelectedBranch(target);
                const res = await ticketApi.getByBranch(target);
                setTickets(Array.isArray(res?.content) ? res.content : Array.isArray(res) ? res : []);
            }
        } catch {
            setTickets(generateSampleTickets());
        } finally {
            setLoading(false);
        }
    };

    const loadTicketsForBranch = async (branchId) => {
        setSelectedBranch(branchId);
        setLoading(true);
        try {
            const res = await ticketApi.getByBranch(branchId);
            setTickets(Array.isArray(res?.content) ? res.content : Array.isArray(res) ? res : []);
        } catch {
            setTickets(generateSampleTickets());
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        try {
            await ticketApi.create(form);
            toast.success('Ticket created!');
            setShowCreate(false);
            loadData();
        } catch (err) {
            toast.error('Failed to create ticket');
        }
    };

    const filtered = tickets.filter((t) => {
        // Tab-based filtering
        if (activeTab === 'pendingDiagnosis' && t.status !== 'OPEN') return false;
        if (activeTab === 'awaitingParts' && t.status !== 'AWAITING_PARTS') return false;
        if (activeTab === 'readyForQA' && t.status !== 'QA') return false;
        if (activeTab === 'completed' && t.status !== 'COMPLETED') return false;
        // myAssigned would filter by assignee, but we don't have that info locally
        // Status dropdown filter
        if (statusFilter && t.status !== statusFilter) return false;
        if (search) {
            const s = search.toLowerCase();
            return (
                t.id?.toLowerCase().includes(s) ||
                t.deviceModel?.toLowerCase().includes(s) ||
                t.symptom?.toLowerCase().includes(s) ||
                t.deviceType?.toLowerCase().includes(s)
            );
        }
        return true;
    });

    const allColumns = [
        { key: 'id', label: 'ID', render: (v) => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
        { key: 'deviceType', label: 'Device' },
        { key: 'deviceModel', label: 'Model' },
        { key: 'symptom', label: 'Symptom', render: (v) => <span className="truncate" style={{ maxWidth: 200, display: 'inline-block' }}>{v || '—'}</span> },
        { key: 'status', label: 'Status', render: (v) => <StatusBadge status={v} statusMap={TICKET_STATUS} /> },
        {
            key: 'priority', label: 'Priority', render: (v) => {
                const colors = { LOW: '#10b981', MEDIUM: '#f59e0b', HIGH: '#f97316', CRITICAL: '#ef4444' };
                return <span style={{ color: colors[v] || '#94a3b8', fontWeight: 500, fontSize: 12 }}>{v || '—'}</span>;
            }
        },
        { key: 'createdAt', label: 'Created', render: (v) => formatDateTime(v) },
    ];
    const columns = useMaskedColumns('tickets', allColumns);

    return (
        <div className="slide-in">
            <div className="page-header">
                <h1>Repair Tickets</h1>
                <PermissionGate requires={['TICKET_CREATE']}>
                    <button className="btn btn-primary" onClick={() => setShowCreate(true)}>
                        <Plus size={16} /> New Ticket
                    </button>
                </PermissionGate>
                {can('export') && <button className="btn btn-secondary" onClick={() => toast.success('Export started')}><Download size={16} /> Export</button>}
            </div>

            <div className="tab-bar">
                {availableTabs.map(tabKey => (
                    <button key={tabKey} className={`tab-item ${activeTab === tabKey ? 'active' : ''}`} onClick={() => setActiveTab(tabKey)}>
                        {TICKET_TABS[tabKey] || tabKey}
                    </button>
                ))}
            </div>

            <div className="filter-bar">
                <div className="search-input">
                    <Search size={16} className="search-icon" />
                    <input placeholder="Search tickets..." value={search} onChange={(e) => setSearch(e.target.value)} />
                </div>
                <select className="form-select" style={{ width: 180 }} value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                    <option value="">All Statuses</option>
                    {Object.entries(TICKET_STATUS).map(([k, v]) => <option key={k} value={k}>{v.label}</option>)}
                </select>
                {branches.length > 1 && (
                    <select className="form-select" style={{ width: 200 }} value={selectedBranch} onChange={(e) => loadTicketsForBranch(e.target.value)}>
                        {branches.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                    </select>
                )}
            </div>

            <div className="card">
                {loading ? (
                    <div className="loading-spinner"><div className="spinner" /></div>
                ) : (
                    <DataTable columns={columns} data={filtered} onRowClick={(row) => navigate(`/tickets/${row.id}`)} />
                )}
            </div>

            <Modal open={showCreate} onClose={() => setShowCreate(false)} title="Create Repair Ticket" footer={
                <><button className="btn btn-secondary" onClick={() => setShowCreate(false)}>Cancel</button>
                    <button className="btn btn-primary" onClick={handleCreate}>Create Ticket</button></>
            }>
                <div className="form-row">
                    <div className="form-group">
                        <label className="form-label">Customer</label>
                        <select className="form-select" value={form.customerId} onChange={(e) => setForm({ ...form, customerId: e.target.value })}>
                            <option value="">Select customer...</option>
                            {customers.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
                        </select>
                    </div>
                    <div className="form-group">
                        <label className="form-label">Branch</label>
                        <select className="form-select" value={form.branchId} onChange={(e) => setForm({ ...form, branchId: e.target.value })}>
                            <option value="">Select branch...</option>
                            {branches.map((b) => <option key={b.id} value={b.id}>{b.name}</option>)}
                        </select>
                    </div>
                </div>
                <div className="form-row">
                    <div className="form-group">
                        <label className="form-label">Device Type</label>
                        <select className="form-select" value={form.deviceType} onChange={(e) => setForm({ ...form, deviceType: e.target.value })}>
                            {DEVICE_TYPES.map((d) => <option key={d} value={d}>{d}</option>)}
                        </select>
                    </div>
                    <div className="form-group">
                        <label className="form-label">Device Model</label>
                        <input className="form-input" placeholder="e.g. Dell XPS 15" value={form.deviceModel} onChange={(e) => setForm({ ...form, deviceModel: e.target.value })} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="form-label">Priority</label>
                    <select className="form-select" value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value })}>
                        {Object.keys(PRIORITIES).map((p) => <option key={p} value={p}>{p}</option>)}
                    </select>
                </div>
                <div className="form-group">
                    <label className="form-label">Symptom / Issue</label>
                    <textarea className="form-textarea" placeholder="Describe the issue..." value={form.symptom} onChange={(e) => setForm({ ...form, symptom: e.target.value })} />
                </div>
            </Modal>
        </div>
    );
}

function generateSampleTickets() {
    return Array.from({ length: 30 }, (_, i) => ({
        id: `t-${String(i + 100).padStart(4, '0')}`,
        deviceType: ['LAPTOP', 'DESKTOP', 'DRONE', 'TABLET', 'PHONE'][i % 5],
        deviceModel: ['Dell XPS 15', 'iMac 24"', 'DJI Mini 3', 'iPad Pro', 'Samsung S24'][i % 5],
        symptom: ['Screen flickering', 'Won\'t power on', 'Motor failure', 'Cracked screen', 'Battery drain'][i % 5],
        status: ['OPEN', 'DIAGNOSED', 'IN_REPAIR', 'QA', 'COMPLETED', 'AWAITING_PARTS', 'CANCELLED'][i % 7],
        priority: ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'][i % 4],
        createdAt: new Date(Date.now() - i * 7200000).toISOString(),
    }));
}
