import { useState, useEffect } from 'react';
import { Plus, Building2, MapPin } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import { shortId } from '../../utils/formatters';
import { branchApi } from '../../api/branches';
import toast from 'react-hot-toast';

const SAMPLE = Array.from({ length: 10 }, (_, i) => ({
    id: `branch-${i}`, name: ['Mumbai Central', 'Delhi NCR', 'Bangalore HSR', 'Hyderabad HITEC', 'Pune Kothrud', 'Chennai Adyar', 'Kolkata Salt Lake', 'Jaipur C-Scheme', 'Ahmedabad SG', 'Lucknow Hazratganj'][i],
    city: ['Mumbai', 'Delhi', 'Bangalore', 'Hyderabad', 'Pune', 'Chennai', 'Kolkata', 'Jaipur', 'Ahmedabad', 'Lucknow'][i],
    tier: [1, 1, 1, 1, 2, 2, 2, 2, 3, 3][i], active: true,
}));

export default function BranchList() {
    const [branches, setBranches] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showCreate, setShowCreate] = useState(false);
    const [form, setForm] = useState({ name: '', city: '', tier: 2 });

    useEffect(() => { load(); }, []);
    const load = async () => {
        setLoading(true);
        try { const d = await branchApi.getAll(); setBranches(Array.isArray(d) ? d : []); }
        catch { setBranches(SAMPLE); } finally { setLoading(false); }
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        try { await branchApi.create(form); toast.success('Branch created'); setShowCreate(false); load(); }
        catch { toast.error('Failed'); }
    };

    const handleDeactivate = async (id) => {
        if (!confirm('Deactivate this branch?')) return;
        try { await branchApi.deactivate(id); toast.success('Branch deactivated'); load(); }
        catch { toast.error('Failed'); }
    };

    const tierColors = { 1: 'var(--accent-blue)', 2: 'var(--accent-amber)', 3: 'var(--accent-emerald)' };

    return (
        <div className="slide-in">
            <div className="page-header"><h1>Branches</h1><button className="btn btn-primary" onClick={() => setShowCreate(true)}><Plus size={16} /> Add Branch</button></div>
            <div className="card">
                {loading ? <div className="loading-spinner"><div className="spinner" /></div> : (
                    <DataTable columns={[
                        { key: 'id', label: 'ID', render: v => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
                        { key: 'name', label: 'Branch Name', render: (v) => <span className="flex items-center gap-8"><Building2 size={14} style={{ color: 'var(--accent-purple)' }} /><b>{v}</b></span> },
                        { key: 'city', label: 'City', render: v => <span className="flex items-center gap-8"><MapPin size={14} style={{ color: 'var(--text-muted)' }} />{v}</span> },
                        { key: 'tier', label: 'Tier', render: v => <span className="status-badge" style={{ color: tierColors[v], background: `${tierColors[v]}18`, border: `1px solid ${tierColors[v]}30` }}>Tier {v}</span> },
                        {
                            key: 'id', label: 'Actions', sortable: false, render: (_, row) => (
                                <div className="table-actions"><button className="btn btn-danger btn-sm" onClick={(e) => { e.stopPropagation(); handleDeactivate(row.id); }}>Deactivate</button></div>
                            )
                        },
                    ]} data={branches} />
                )}
            </div>
            <Modal open={showCreate} onClose={() => setShowCreate(false)} title="Add Branch" footer={
                <><button className="btn btn-secondary" onClick={() => setShowCreate(false)}>Cancel</button><button className="btn btn-primary" onClick={handleCreate}>Create</button></>
            }>
                <div className="form-group"><label className="form-label">Branch Name</label><input className="form-input" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Mumbai Central" /></div>
                <div className="form-row">
                    <div className="form-group"><label className="form-label">City</label><input className="form-input" value={form.city} onChange={e => setForm({ ...form, city: e.target.value })} placeholder="Mumbai" /></div>
                    <div className="form-group"><label className="form-label">Tier</label><select className="form-select" value={form.tier} onChange={e => setForm({ ...form, tier: Number(e.target.value) })}><option value={1}>Tier 1 (Metro)</option><option value={2}>Tier 2</option><option value={3}>Tier 3</option></select></div>
                </div>
            </Modal>
        </div>
    );
}
