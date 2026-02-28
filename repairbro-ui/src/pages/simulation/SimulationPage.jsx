import { useState, useEffect } from 'react';
import { Plus, Play, FlaskConical, BarChart3 } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import { formatDateTime, shortId, formatCurrency } from '../../utils/formatters';
import { simulationApi } from '../../api/simulation';
import toast from 'react-hot-toast';

const SAMPLE_S = [
    { id: 's1', name: '10 Branch Expansion', branches: 10, avgDemandPerDay: 30, demandDist: 'poisson', juniorRatio: 0.6, inventoryStrategy: 'CENTRALIZED', status: 'COMPLETED' },
    { id: 's2', name: '50 Branch Scale Test', branches: 50, avgDemandPerDay: 25, demandDist: 'poisson', juniorRatio: 0.5, inventoryStrategy: 'DISTRIBUTED', status: 'PENDING' },
];
const SAMPLE_R = [
    { id: 'r1', branchLabel: 'Branch 1', revenue: 1350000, costs: 980000, profit: 370000, ftfr: 78.5, mttrHours: 18.2, slaBreaches: 3, stockouts: 1 },
    { id: 'r2', branchLabel: 'Branch 2', revenue: 1100000, costs: 850000, profit: 250000, ftfr: 82.1, mttrHours: 15.8, slaBreaches: 1, stockouts: 0 },
];

export default function SimulationPage() {
    const [scenarios, setScenarios] = useState([]);
    const [results, setResults] = useState([]);
    const [selected, setSelected] = useState(null);
    const [showCreate, setShowCreate] = useState(false);
    const [form, setForm] = useState({ name: '', branches: 10, avgDemandPerDay: 30, juniorRatio: 0.6, inventoryStrategy: 'CENTRALIZED' });

    useEffect(() => { load(); }, []);
    const load = async () => {
        try { const d = await simulationApi.listScenarios(); setScenarios(Array.isArray(d) ? d : []); }
        catch { setScenarios(SAMPLE_S); }
    };

    const handleRun = async (id) => {
        try { const r = await simulationApi.run(id); setResults(Array.isArray(r) ? r : []); setSelected(id); toast.success('Simulation complete!'); }
        catch { setResults(SAMPLE_R); setSelected(id); toast.success('Demo simulation complete'); }
    };

    const handleCreate = async () => {
        try { await simulationApi.createScenario(form); toast.success('Scenario created'); setShowCreate(false); load(); }
        catch { toast.error('Failed'); }
    };

    return (
        <div className="slide-in">
            <div className="page-header"><h1>Simulation Lab</h1><button className="btn btn-primary" onClick={() => setShowCreate(true)}><Plus size={16} /> New Scenario</button></div>
            <div className="card mb-24">
                <DataTable columns={[
                    { key: 'name', label: 'Scenario', render: v => <b>{v}</b> },
                    { key: 'branches', label: 'Branches' },
                    { key: 'avgDemandPerDay', label: 'Demand/Day' },
                    { key: 'inventoryStrategy', label: 'Inventory' },
                    { key: 'status', label: 'Status', render: v => <span className="status-badge" style={{ color: v === 'COMPLETED' ? 'var(--accent-emerald)' : 'var(--accent-amber)', background: v === 'COMPLETED' ? 'var(--accent-emerald-glow)' : 'var(--accent-amber-glow)' }}><span className="dot" />{v}</span> },
                    { key: 'id', label: 'Actions', sortable: false, render: (_, row) => <button className="btn btn-primary btn-sm" onClick={() => handleRun(row.id)}><Play size={14} /> Run</button> },
                ]} data={scenarios} />
            </div>

            {selected && results.length > 0 && (
                <div className="card">
                    <div className="card-header"><h3 className="card-title">Simulation Results</h3></div>
                    <DataTable columns={[
                        { key: 'branchLabel', label: 'Branch' },
                        { key: 'revenue', label: 'Revenue', render: v => formatCurrency(v) },
                        { key: 'costs', label: 'Costs', render: v => formatCurrency(v) },
                        { key: 'profit', label: 'Profit', render: v => <b style={{ color: v > 0 ? 'var(--accent-emerald)' : 'var(--accent-red)' }}>{formatCurrency(v)}</b> },
                        { key: 'ftfr', label: 'FTFR', render: v => `${v}%` },
                        { key: 'mttrHours', label: 'MTTR', render: v => `${v}h` },
                        { key: 'slaBreaches', label: 'SLA Breaches', render: v => <span style={{ color: v > 2 ? 'var(--accent-red)' : 'var(--accent-emerald)' }}>{v}</span> },
                    ]} data={results} />
                </div>
            )}

            <Modal open={showCreate} onClose={() => setShowCreate(false)} title="New Scenario" footer={<><button className="btn btn-secondary" onClick={() => setShowCreate(false)}>Cancel</button><button className="btn btn-primary" onClick={handleCreate}>Create</button></>}>
                <div className="form-group"><label className="form-label">Name</label><input className="form-input" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Scenario name" /></div>
                <div className="form-row">
                    <div className="form-group"><label className="form-label">Branches</label><input className="form-input" type="number" value={form.branches} onChange={e => setForm({ ...form, branches: Number(e.target.value) })} /></div>
                    <div className="form-group"><label className="form-label">Avg Demand/Day</label><input className="form-input" type="number" value={form.avgDemandPerDay} onChange={e => setForm({ ...form, avgDemandPerDay: Number(e.target.value) })} /></div>
                </div>
                <div className="form-row">
                    <div className="form-group"><label className="form-label">Junior Ratio</label><input className="form-input" type="number" step="0.1" value={form.juniorRatio} onChange={e => setForm({ ...form, juniorRatio: Number(e.target.value) })} /></div>
                    <div className="form-group"><label className="form-label">Inventory Strategy</label><select className="form-select" value={form.inventoryStrategy} onChange={e => setForm({ ...form, inventoryStrategy: e.target.value })}><option>CENTRALIZED</option><option>DISTRIBUTED</option></select></div>
                </div>
            </Modal>
        </div>
    );
}
