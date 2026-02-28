import { useState, useEffect } from 'react';
import { ShieldCheck, AlertTriangle, Plus } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import KpiCard from '../../components/KpiCard';
import { formatDateTime, shortId } from '../../utils/formatters';
import { slaApi } from '../../api/sla';
import { branchApi } from '../../api/branches';
import toast from 'react-hot-toast';

const SAMPLE_SLA = Array.from({ length: 10 }, (_, i) => ({
    id: `sla-${i}`, ticketId: `t-${1000 + i}`, slaType: ['DIAGNOSIS', 'REPAIR', 'PARTS_DELIVERY', 'QA'][i % 4],
    deadline: new Date(Date.now() + (i % 3 === 0 ? -86400000 : 172800000)).toISOString(),
    breached: i % 3 === 0, createdAt: new Date(Date.now() - i * 86400000).toISOString(),
}));
const SAMPLE_COMP = [
    { id: 'c1', branchId: 'b1', customerName: 'Priya Patel', ticketId: 't-1002', severity: 'HIGH', description: 'Repair took 10 days instead of promised 3', status: 'OPEN', createdAt: '2026-02-25T10:00:00Z' },
    { id: 'c2', branchId: 'b1', customerName: 'Amit Kumar', ticketId: 't-1005', severity: 'MEDIUM', description: 'Wrong part installed, had to redo repair', status: 'RESOLVED', createdAt: '2026-02-20T14:30:00Z' },
];

export default function SlaPage() {
    const [tab, setTab] = useState('sla');
    const [slas, setSlas] = useState([]);
    const [complaints, setComplaints] = useState([]);
    const [branches, setBranches] = useState([]);
    const [selectedBranch, setSelectedBranch] = useState('');

    useEffect(() => {
        loadBranches();
    }, []);

    const loadBranches = async () => {
        try { const d = await branchApi.getAll(); setBranches(Array.isArray(d) ? d : []); }
        catch { setBranches([{ id: 'b1', name: 'Mumbai Central' }]); }
    };

    const loadData = async (branchId) => {
        setSelectedBranch(branchId);
        try { const [s, c] = await Promise.all([slaApi.getByBranch(branchId), slaApi.getComplaints(branchId)]); setSlas(Array.isArray(s) ? s : []); setComplaints(Array.isArray(c) ? c : []); }
        catch { setSlas(SAMPLE_SLA); setComplaints(SAMPLE_COMP); }
    };

    const breachedCount = slas.filter(s => s.breached).length;
    const onTrackCount = slas.filter(s => !s.breached).length;

    return (
        <div className="slide-in">
            <div className="page-header"><h1>SLA Monitor</h1></div>
            <div className="filter-bar">
                <select className="form-select" style={{ width: 280 }} value={selectedBranch} onChange={e => loadData(e.target.value)}>
                    <option value="">Select Branch...</option>
                    {branches.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                </select>
            </div>

            {selectedBranch && (
                <>
                    <div className="kpi-grid">
                        <KpiCard icon={ShieldCheck} label="On Track" value={onTrackCount} accent="emerald" />
                        <KpiCard icon={AlertTriangle} label="Breached" value={breachedCount} accent="red" />
                    </div>

                    <div className="tab-bar">
                        <button className={`tab-item ${tab === 'sla' ? 'active' : ''}`} onClick={() => setTab('sla')}>SLA Records</button>
                        <button className={`tab-item ${tab === 'complaints' ? 'active' : ''}`} onClick={() => setTab('complaints')}>Complaints</button>
                    </div>

                    {tab === 'sla' && (
                        <div className="card">
                            <DataTable columns={[
                                { key: 'ticketId', label: 'Ticket', render: v => <span style={{ fontFamily: 'monospace' }}>{shortId(v)}</span> },
                                { key: 'slaType', label: 'SLA Type', render: v => <span className="status-badge" style={{ color: 'var(--accent-cyan)', background: 'rgba(6,182,212,0.1)' }}>{v}</span> },
                                { key: 'deadline', label: 'Deadline', render: v => formatDateTime(v) },
                                {
                                    key: 'breached', label: 'Status', render: v => v
                                        ? <span className="status-badge" style={{ color: 'var(--accent-red)', background: 'var(--accent-red-glow)' }}><span className="dot" /> BREACHED</span>
                                        : <span className="status-badge" style={{ color: 'var(--accent-emerald)', background: 'var(--accent-emerald-glow)' }}><span className="dot" /> ON TRACK</span>
                                },
                            ]} data={slas} />
                        </div>
                    )}

                    {tab === 'complaints' && (
                        <div className="card">
                            <DataTable columns={[
                                { key: 'customerName', label: 'Customer', render: v => <b>{v}</b> },
                                {
                                    key: 'severity', label: 'Severity', render: v => {
                                        const c = { HIGH: 'var(--accent-red)', MEDIUM: 'var(--accent-amber)', LOW: 'var(--accent-emerald)' };
                                        return <span className="status-badge" style={{ color: c[v], background: `${c[v]}18` }}>{v}</span>;
                                    }
                                },
                                { key: 'description', label: 'Description', render: v => <span className="truncate" style={{ maxWidth: 250, display: 'inline-block' }}>{v}</span> },
                                { key: 'status', label: 'Status', render: v => <span className="status-badge" style={{ color: v === 'RESOLVED' ? 'var(--accent-emerald)' : 'var(--accent-amber)', background: v === 'RESOLVED' ? 'var(--accent-emerald-glow)' : 'var(--accent-amber-glow)' }}><span className="dot" />{v}</span> },
                                { key: 'createdAt', label: 'Filed', render: v => formatDateTime(v) },
                            ]} data={complaints} />
                        </div>
                    )}
                </>
            )}
        </div>
    );
}
