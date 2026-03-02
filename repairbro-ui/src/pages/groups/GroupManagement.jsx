import { useState, useEffect } from 'react';
import { Plus, Shield, ChevronRight, Trash2, Check, X } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import { groupApi } from '../../api/groups';
import { GROUP_LEVEL_COLORS } from '../../utils/constants';
import { useActionPermission } from '../../hooks/useActionPermission';
import toast from 'react-hot-toast';

const SAMPLE_GROUPS = [
    { id: 'g1', name: 'Super Admin', description: 'Full system access', level: 'EXECUTIVE', systemDefined: true, active: true, permissions: [] },
    { id: 'g2', name: 'Regional Manager', description: 'Multi-branch oversight', level: 'MANAGEMENT', systemDefined: true, active: true, permissions: [] },
    { id: 'g3', name: 'Service Manager', description: 'Single branch operations', level: 'MANAGEMENT', systemDefined: true, active: true, permissions: [] },
    { id: 'g4', name: 'Service Senior Executive', description: 'Senior tech with billing', level: 'OPERATIONAL', systemDefined: true, active: true, permissions: [] },
    { id: 'g5', name: 'Service Executive', description: 'Standard technician', level: 'OPERATIONAL', systemDefined: true, active: true, permissions: [] },
    { id: 'g6', name: 'Service Intern', description: 'Trainee — view only', level: 'OPERATIONAL', systemDefined: true, active: true, permissions: [] },
    { id: 'g7', name: 'Store Counter Executive', description: 'Customer intake', level: 'OPERATIONAL', systemDefined: true, active: true, permissions: [] },
    { id: 'g8', name: 'Billing Admin', description: 'Full billing access', level: 'FINANCE', systemDefined: true, active: true, permissions: [] },
    { id: 'g9', name: 'Billing Viewer', description: 'Read-only billing', level: 'FINANCE', systemDefined: true, active: true, permissions: [] },
    { id: 'g10', name: 'Inventory Manager', description: 'Parts & stock management', level: 'LOGISTICS', systemDefined: true, active: true, permissions: [] },
    { id: 'g11', name: 'Franchise Owner', description: 'Own franchise data', level: 'PARTNER', systemDefined: true, active: true, permissions: [] },
    { id: 'g12', name: 'Customer', description: 'View own tickets only', level: 'EXTERNAL', systemDefined: true, active: true, permissions: [] },
];

const SAMPLE_PERMISSIONS = [
    { id: 'p1', code: 'TICKET_CREATE', name: 'Create Ticket', module: 'TICKET' },
    { id: 'p2', code: 'TICKET_VIEW', name: 'View Ticket', module: 'TICKET' },
    { id: 'p3', code: 'TICKET_ASSIGN', name: 'Assign Technician', module: 'TICKET' },
    { id: 'p4', code: 'CUSTOMER_CREATE', name: 'Create Customer', module: 'CUSTOMER' },
    { id: 'p5', code: 'CUSTOMER_VIEW', name: 'View Customer', module: 'CUSTOMER' },
    { id: 'p6', code: 'INVOICE_CREATE', name: 'Create Invoice', module: 'BILLING' },
    { id: 'p7', code: 'INVOICE_VIEW', name: 'View Invoice', module: 'BILLING' },
    { id: 'p8', code: 'BRANCH_VIEW', name: 'View Branch', module: 'BRANCH' },
    { id: 'p9', code: 'GROUP_VIEW', name: 'View Groups', module: 'ADMIN' },
    { id: 'p10', code: 'GROUP_CREATE', name: 'Create Group', module: 'ADMIN' },
];

export default function GroupManagement() {
    const { canCreate, can } = useActionPermission('groups');
    const [groups, setGroups] = useState([]);
    const [permissions, setPermissions] = useState([]);
    const [selected, setSelected] = useState(null);
    const [showCreate, setShowCreate] = useState(false);
    const [showPerms, setShowPerms] = useState(false);
    const [form, setForm] = useState({ name: '', description: '', level: 'OPERATIONAL', permissionCodes: [] });
    const [loading, setLoading] = useState(false);

    useEffect(() => { loadAll(); }, []);

    const loadAll = async () => {
        setLoading(true);
        try {
            const [g, p] = await Promise.all([groupApi.getAll(), groupApi.getPermissions()]);
            setGroups(Array.isArray(g) ? g : []);
            setPermissions(Array.isArray(p) ? p : []);
        } catch {
            setGroups(SAMPLE_GROUPS);
            setPermissions(SAMPLE_PERMISSIONS);
        } finally { setLoading(false); }
    };

    const handleCreate = async () => {
        try {
            await groupApi.create(form);
            toast.success('Group created');
            setShowCreate(false);
            setForm({ name: '', description: '', level: 'OPERATIONAL', permissionCodes: [] });
            loadAll();
        } catch { toast.error('Failed to create group'); }
    };

    const handleDelete = async (id) => {
        if (!confirm('Delete this group?')) return;
        try { await groupApi.remove(id); toast.success('Deleted'); loadAll(); }
        catch (e) { toast.error(e?.response?.data?.message || 'Cannot delete'); }
    };

    const handleSelect = async (group) => {
        try {
            const full = await groupApi.getById(group.id);
            setSelected(full || group);
        } catch { setSelected(group); }
        setShowPerms(true);
    };

    // Group permissions by module for the permission assignment view
    const permsByModule = permissions.reduce((acc, p) => {
        (acc[p.module] = acc[p.module] || []).push(p);
        return acc;
    }, {});

    const selectedPermCodes = new Set((selected?.permissions || []).map(p => p.code));

    const togglePermission = async (perm) => {
        if (!selected) return;
        try {
            if (selectedPermCodes.has(perm.code)) {
                await groupApi.removePermission(selected.id, perm.id);
            } else {
                await groupApi.addPermission(selected.id, perm.id);
            }
            const updated = await groupApi.getById(selected.id);
            setSelected(updated || selected);
            loadAll();
        } catch { toast.error('Failed to update'); }
    };

    return (
        <div className="slide-in">
            <div className="page-header">
                <h1>Groups & Permissions</h1>
                {canCreate && <button className="btn btn-primary" onClick={() => setShowCreate(true)}>
                    <Plus size={16} /> New Group
                </button>}
            </div>

            <div className="card">
                {loading ? <div className="loading-spinner"><div className="spinner" /></div> : (
                    <DataTable columns={[
                        {
                            key: 'name', label: 'Group Name', render: (v, row) => (
                                <span style={{ display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer' }} onClick={() => handleSelect(row)}>
                                    <Shield size={16} style={{ color: GROUP_LEVEL_COLORS[row.level] || '#94a3b8' }} />
                                    <b>{v}</b>
                                    {row.systemDefined && <span style={{ fontSize: 10, color: '#64748b', background: '#1e293b', padding: '2px 6px', borderRadius: 4 }}>SYSTEM</span>}
                                </span>
                            )
                        },
                        { key: 'description', label: 'Description' },
                        {
                            key: 'level', label: 'Level', render: v => (
                                <span className="status-badge" style={{ color: GROUP_LEVEL_COLORS[v] || '#94a3b8', background: `${GROUP_LEVEL_COLORS[v] || '#94a3b8'}18` }}>{v}</span>
                            )
                        },
                        {
                            key: 'permissions', label: 'Permissions', render: v => (
                                <span style={{ color: 'var(--accent-cyan)' }}>{Array.isArray(v) ? v.length : '—'}</span>
                            )
                        },
                        {
                            key: 'id', label: '', sortable: false, render: (_, row) => !row.systemDefined && can('delete') && (
                                <button className="btn btn-secondary btn-sm" onClick={() => handleDelete(row.id)}><Trash2 size={14} /></button>
                            )
                        },
                    ]} data={groups} />
                )}
            </div>

            {/* Create Group Modal */}
            <Modal open={showCreate} onClose={() => setShowCreate(false)} title="Create Permission Group"
                footer={<><button className="btn btn-secondary" onClick={() => setShowCreate(false)}>Cancel</button><button className="btn btn-primary" onClick={handleCreate}>Create</button></>}>
                <div className="form-group"><label className="form-label">Group Name</label><input className="form-input" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="e.g. QA Specialist" /></div>
                <div className="form-group"><label className="form-label">Description</label><input className="form-input" value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} placeholder="What this group can do" /></div>
                <div className="form-group"><label className="form-label">Level</label>
                    <select className="form-select" value={form.level} onChange={e => setForm({ ...form, level: e.target.value })}>
                        {['EXECUTIVE', 'MANAGEMENT', 'OPERATIONAL', 'FINANCE', 'LOGISTICS', 'PARTNER', 'EXTERNAL'].map(l => <option key={l}>{l}</option>)}
                    </select>
                </div>
            </Modal>

            {/* Permission Assignment Modal */}
            <Modal open={showPerms} onClose={() => setShowPerms(false)} title={`Permissions — ${selected?.name || ''}`}>
                <div style={{ maxHeight: 400, overflowY: 'auto' }}>
                    {Object.entries(permsByModule).map(([mod, perms]) => (
                        <div key={mod} style={{ marginBottom: 16 }}>
                            <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--accent-cyan)', textTransform: 'uppercase', letterSpacing: 1, marginBottom: 8 }}>{mod}</div>
                            {perms.map(p => {
                                const checked = selectedPermCodes.has(p.code);
                                return (
                                    <div key={p.id} onClick={() => togglePermission(p)} style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '8px 12px', borderRadius: 8, cursor: 'pointer', marginBottom: 4, background: checked ? 'rgba(59,130,246,0.1)' : 'transparent', border: `1px solid ${checked ? 'rgba(59,130,246,0.3)' : 'transparent'}`, transition: 'all 0.2s' }}>
                                        <div style={{ width: 20, height: 20, borderRadius: 4, border: `2px solid ${checked ? 'var(--accent-blue)' : '#475569'}`, display: 'flex', alignItems: 'center', justifyContent: 'center', background: checked ? 'var(--accent-blue)' : 'transparent' }}>
                                            {checked && <Check size={12} color="white" />}
                                        </div>
                                        <div style={{ flex: 1 }}>
                                            <div style={{ fontSize: 13, fontWeight: 500 }}>{p.name}</div>
                                            <div style={{ fontSize: 11, color: '#64748b', fontFamily: 'monospace' }}>{p.code}</div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    ))}
                </div>
            </Modal>
        </div>
    );
}
