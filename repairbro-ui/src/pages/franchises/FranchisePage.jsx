import { useState, useEffect } from 'react';
import { Plus, Store, DollarSign, Calculator } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import KpiCard from '../../components/KpiCard';
import { formatCurrency, formatDateTime, shortId } from '../../utils/formatters';
import { franchiseApi } from '../../api/franchises';
import toast from 'react-hot-toast';

const SAMPLE_F = [
    { id: 'f1', name: 'RepairBro Mumbai West', ownerName: 'Rajesh Patel', city: 'Mumbai', tier: 'PREMIUM', status: 'ACTIVE', royaltyPercent: 7, setupFee: 400000, onboardedAt: '2025-06-15T00:00:00Z' },
    { id: 'f2', name: 'RepairBro Delhi South', ownerName: 'Anjali Verma', city: 'Delhi', tier: 'STANDARD', status: 'ACTIVE', royaltyPercent: 6, setupFee: 250000, onboardedAt: '2025-08-20T00:00:00Z' },
    { id: 'f3', name: 'RepairBro Pune', ownerName: 'Sunil Joshi', city: 'Pune', tier: 'STANDARD', status: 'PENDING', royaltyPercent: 7, setupFee: 250000, onboardedAt: '2026-01-10T00:00:00Z' },
];
const SAMPLE_R = [
    { id: 'r1', period: '2026-01', grossRevenue: 1350000, royaltyAmount: 94500, status: 'PAID', calculatedAt: '2026-02-01T00:00:00Z' },
    { id: 'r2', period: '2025-12', grossRevenue: 1200000, royaltyAmount: 84000, status: 'PAID', calculatedAt: '2026-01-01T00:00:00Z' },
];

export default function FranchisePage() {
    const [tab, setTab] = useState('franchises');
    const [franchises, setFranchises] = useState([]);
    const [royalties, setRoyalties] = useState([]);
    const [selectedFranchise, setSelectedFranchise] = useState('');

    useEffect(() => {
        loadFranchises();
    }, []);

    const loadFranchises = async () => {
        try { const d = await franchiseApi.getAll(); setFranchises(Array.isArray(d) ? d : []); }
        catch { setFranchises(SAMPLE_F); }
    };

    const loadRoyalties = async (fId) => {
        setSelectedFranchise(fId);
        try { const d = await franchiseApi.getRoyalties(fId); setRoyalties(Array.isArray(d) ? d : []); }
        catch { setRoyalties(SAMPLE_R); }
    };

    const tierColor = { PREMIUM: 'var(--accent-purple)', STANDARD: 'var(--accent-blue)' };
    const statusColor = { ACTIVE: 'var(--accent-emerald)', PENDING: 'var(--accent-amber)', INACTIVE: 'var(--accent-red)' };

    return (
        <div className="slide-in">
            <div className="page-header"><h1>Franchise Hub</h1></div>
            <div className="tab-bar">
                <button className={`tab-item ${tab === 'franchises' ? 'active' : ''}`} onClick={() => setTab('franchises')}>Franchises</button>
                <button className={`tab-item ${tab === 'royalties' ? 'active' : ''}`} onClick={() => setTab('royalties')}>Royalties</button>
            </div>

            {tab === 'franchises' && (
                <div className="card">
                    <DataTable columns={[
                        { key: 'name', label: 'Franchise', render: v => <span className="flex items-center gap-8"><Store size={14} style={{ color: 'var(--accent-purple)' }} /><b>{v}</b></span> },
                        { key: 'ownerName', label: 'Owner' },
                        { key: 'city', label: 'City' },
                        { key: 'tier', label: 'Tier', render: v => <span className="status-badge" style={{ color: tierColor[v], background: `${tierColor[v]}18` }}>{v}</span> },
                        { key: 'status', label: 'Status', render: v => <span className="status-badge" style={{ color: statusColor[v], background: `${statusColor[v]}18` }}><span className="dot" />{v}</span> },
                        { key: 'royaltyPercent', label: 'Royalty', render: v => `${v}%` },
                        { key: 'setupFee', label: 'Setup Fee', render: v => formatCurrency(v) },
                    ]} data={franchises} />
                </div>
            )}

            {tab === 'royalties' && (
                <>
                    <div className="filter-bar">
                        <select className="form-select" style={{ width: 280 }} value={selectedFranchise} onChange={e => loadRoyalties(e.target.value)}>
                            <option value="">Select Franchise...</option>
                            {franchises.map(f => <option key={f.id} value={f.id}>{f.name}</option>)}
                        </select>
                    </div>
                    {selectedFranchise && (
                        <div className="card">
                            <DataTable columns={[
                                { key: 'period', label: 'Period', render: v => <b>{v}</b> },
                                { key: 'grossRevenue', label: 'Gross Revenue', render: v => formatCurrency(v) },
                                { key: 'royaltyAmount', label: 'Royalty Due', render: v => <b style={{ color: 'var(--accent-amber)' }}>{formatCurrency(v)}</b> },
                                { key: 'status', label: 'Status', render: v => <span className="status-badge" style={{ color: v === 'PAID' ? 'var(--accent-emerald)' : 'var(--accent-amber)', background: v === 'PAID' ? 'var(--accent-emerald-glow)' : 'var(--accent-amber-glow)' }}><span className="dot" />{v}</span> },
                                { key: 'calculatedAt', label: 'Calculated', render: v => formatDateTime(v) },
                            ]} data={royalties} />
                        </div>
                    )}
                </>
            )}
        </div>
    );
}
