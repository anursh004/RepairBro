import { useState, useEffect } from 'react';
import { Plus, Wrench } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import { shortId } from '../../utils/formatters';
import { technicianApi } from '../../api/technicians';
import { useActionPermission } from '../../hooks/useActionPermission';
import { useMaskedColumns } from '../../hooks/useMaskedColumns';
import toast from 'react-hot-toast';

const SAMPLE = Array.from({ length: 8 }, (_, i) => ({
    id: `tp-${i}`, userId: `u-${i + 2}`, name: ['Vikram Patel', 'Priya Singh', 'Rahul Dev', 'Sneha Iyer', 'Amit Chandra', 'Deepa Nair', 'Suresh Kumar', 'Anita Rao'][i],
    specialization: ['LAPTOP', 'DESKTOP', 'DRONE', 'LAPTOP', 'DESKTOP', 'DRONE', 'LAPTOP', 'DESKTOP'][i],
    certLevel: ['SENIOR', 'CERTIFIED', 'SPECIALIST', 'JUNIOR', 'SENIOR', 'CERTIFIED', 'JUNIOR', 'SENIOR'][i],
    branchName: ['Mumbai Central', 'Mumbai Central', 'Delhi NCR', 'Delhi NCR', 'Bangalore HSR', 'Bangalore HSR', 'Pune', 'Pune'][i],
}));

export default function TechnicianList() {
    const { canCreate } = useActionPermission('technicians');
    const [techs, setTechs] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showCreate, setShowCreate] = useState(false);
    const [form, setForm] = useState({ userId: '', branchId: '', specialization: 'LAPTOP', certLevel: 'JUNIOR' });

    useEffect(() => { load(); }, []);
    const load = async () => {
        setLoading(true);
        try { const d = await technicianApi.getAll(); setTechs(Array.isArray(d) ? d : []); }
        catch { setTechs(SAMPLE); } finally { setLoading(false); }
    };

    const certColors = { JUNIOR: 'var(--accent-amber)', CERTIFIED: 'var(--accent-blue)', SENIOR: 'var(--accent-emerald)', SPECIALIST: 'var(--accent-purple)' };

    const allColumns = [
        { key: 'name', label: 'Name', render: v => <span className="flex items-center gap-8"><Wrench size={14} style={{ color: 'var(--accent-blue)' }} /><b>{v}</b></span> },
        { key: 'specialization', label: 'Specialization', render: v => <span className="status-badge" style={{ color: 'var(--accent-cyan)', background: 'rgba(6,182,212,0.1)' }}>{v}</span> },
        { key: 'certLevel', label: 'Cert Level', render: v => <span className="status-badge" style={{ color: certColors[v], background: `${certColors[v]}18` }}>{v}</span> },
        { key: 'branchName', label: 'Branch' },
    ];
    const columns = useMaskedColumns('technicians', allColumns);

    return (
        <div className="slide-in">
            <div className="page-header"><h1>Technicians</h1>{canCreate && <button className="btn btn-primary" onClick={() => setShowCreate(true)}><Plus size={16} /> Add Technician</button>}</div>
            <div className="card">
                {loading ? <div className="loading-spinner"><div className="spinner" /></div> : (
                    <DataTable columns={columns} data={techs} />
                )}
            </div>
            <Modal open={showCreate} onClose={() => setShowCreate(false)} title="Add Technician" footer={<><button className="btn btn-secondary" onClick={() => setShowCreate(false)}>Cancel</button><button className="btn btn-primary" onClick={async () => { try { await technicianApi.create(form); toast.success('Created'); setShowCreate(false); load(); } catch { toast.error('Failed'); } }}>Create</button></>}>
                <div className="form-group"><label className="form-label">User ID</label><input className="form-input" value={form.userId} onChange={e => setForm({ ...form, userId: e.target.value })} placeholder="User UUID" /></div>
                <div className="form-group"><label className="form-label">Branch ID</label><input className="form-input" value={form.branchId} onChange={e => setForm({ ...form, branchId: e.target.value })} placeholder="Branch UUID" /></div>
                <div className="form-row">
                    <div className="form-group"><label className="form-label">Specialization</label><select className="form-select" value={form.specialization} onChange={e => setForm({ ...form, specialization: e.target.value })}><option>LAPTOP</option><option>DESKTOP</option><option>DRONE</option></select></div>
                    <div className="form-group"><label className="form-label">Cert Level</label><select className="form-select" value={form.certLevel} onChange={e => setForm({ ...form, certLevel: e.target.value })}><option>JUNIOR</option><option>CERTIFIED</option><option>SENIOR</option><option>SPECIALIST</option></select></div>
                </div>
            </Modal>
        </div>
    );
}
