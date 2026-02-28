import { useState, useEffect } from 'react';
import { Package, AlertTriangle, Plus, ShoppingCart } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import KpiCard from '../../components/KpiCard';
import { formatCurrency, shortId } from '../../utils/formatters';
import { inventoryApi } from '../../api/inventory';
import { branchApi } from '../../api/branches';
import toast from 'react-hot-toast';

const SAMPLE_PARTS = Array.from({ length: 15 }, (_, i) => ({
    id: `part-${i}`, sku: `SKU-${1000 + i}`, name: ['Thermal Paste', 'SSD 256GB', 'RAM 8GB DDR4', 'Display Cable', 'Battery Pack', 'Cooling Fan', 'Keyboard', 'Charger 65W', 'Trackpad', 'Motherboard', 'GPU Fan', 'WiFi Card', 'USB Port', 'Hinge Set', 'Speaker'][i],
    costPrice: [150, 2800, 2200, 450, 3500, 800, 1200, 1500, 900, 8500, 1100, 600, 200, 350, 400][i],
    retailPrice: [300, 4500, 3800, 800, 5500, 1400, 2000, 2500, 1500, 14000, 1800, 1000, 400, 600, 700][i],
}));

const SAMPLE_INV = Array.from({ length: 15 }, (_, i) => ({
    sparePartId: `part-${i}`, partName: SAMPLE_PARTS[i].name, qty: Math.floor(Math.random() * 30), reorderLevel: 5,
}));

export default function InventoryPage() {
    const [tab, setTab] = useState('parts');
    const [parts, setParts] = useState([]);
    const [inventory, setInventory] = useState([]);
    const [branches, setBranches] = useState([]);
    const [selectedBranch, setSelectedBranch] = useState('');
    const [loading, setLoading] = useState(false);
    const [showAddPart, setShowAddPart] = useState(false);
    const [partForm, setPartForm] = useState({ sku: '', name: '', costPrice: '', retailPrice: '' });

    useEffect(() => { loadParts(); loadBranches(); }, []);

    const loadParts = async () => {
        setLoading(true);
        try { const d = await inventoryApi.getParts(); setParts(Array.isArray(d) ? d : []); }
        catch { setParts(SAMPLE_PARTS); } finally { setLoading(false); }
    };

    const loadBranches = async () => {
        try { const d = await branchApi.getAll(); setBranches(Array.isArray(d) ? d : []); }
        catch { setBranches([{ id: 'b1', name: 'Mumbai Central' }, { id: 'b2', name: 'Delhi NCR' }]); }
    };

    const loadInventory = async (branchId) => {
        setSelectedBranch(branchId);
        if (!branchId) return;
        try { const d = await inventoryApi.getInventory(branchId); setInventory(Array.isArray(d) ? d : []); }
        catch { setInventory(SAMPLE_INV); }
    };

    const handleAddPart = async () => {
        try { await inventoryApi.createPart({ ...partForm, costPrice: Number(partForm.costPrice), retailPrice: Number(partForm.retailPrice) }); toast.success('Part added'); setShowAddPart(false); loadParts(); }
        catch { toast.error('Failed'); }
    };

    const lowStockCount = inventory.filter(i => i.qty <= (i.reorderLevel || 5)).length;

    return (
        <div className="slide-in">
            <div className="page-header"><h1>Inventory & Parts</h1><button className="btn btn-primary" onClick={() => setShowAddPart(true)}><Plus size={16} /> Add Part</button></div>

            <div className="tab-bar">
                <button className={`tab-item ${tab === 'parts' ? 'active' : ''}`} onClick={() => setTab('parts')}>Parts Catalog</button>
                <button className={`tab-item ${tab === 'inventory' ? 'active' : ''}`} onClick={() => setTab('inventory')}>Branch Inventory</button>
            </div>

            {tab === 'parts' && (
                <div className="card">
                    {loading ? <div className="loading-spinner"><div className="spinner" /></div> : (
                        <DataTable columns={[
                            { key: 'sku', label: 'SKU', render: v => <span style={{ fontFamily: 'monospace', color: 'var(--accent-cyan)' }}>{v}</span> },
                            { key: 'name', label: 'Part Name', render: v => <b>{v}</b> },
                            { key: 'costPrice', label: 'Cost', render: v => formatCurrency(v) },
                            { key: 'retailPrice', label: 'Retail', render: v => formatCurrency(v) },
                            {
                                key: 'retailPrice', label: 'Margin', sortable: false, render: (_, row) => {
                                    const margin = row.retailPrice && row.costPrice ? Math.round(((row.retailPrice - row.costPrice) / row.retailPrice) * 100) : 0;
                                    return <span style={{ color: margin > 40 ? 'var(--accent-emerald)' : 'var(--accent-amber)' }}>{margin}%</span>;
                                }
                            },
                        ]} data={parts} />
                    )}
                </div>
            )}

            {tab === 'inventory' && (
                <>
                    <div className="filter-bar">
                        <select className="form-select" style={{ width: 260 }} value={selectedBranch} onChange={e => loadInventory(e.target.value)}>
                            <option value="">Select Branch...</option>
                            {branches.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                        </select>
                    </div>
                    {selectedBranch && (
                        <>
                            <div className="kpi-grid" style={{ marginBottom: 20 }}>
                                <KpiCard icon={Package} label="Total SKUs" value={inventory.length} accent="blue" />
                                <KpiCard icon={AlertTriangle} label="Low Stock" value={lowStockCount} accent="red" />
                            </div>
                            <div className="card">
                                <DataTable columns={[
                                    { key: 'partName', label: 'Part', render: v => <b>{v}</b> },
                                    { key: 'qty', label: 'Quantity', render: (v, row) => <span style={{ color: v <= (row.reorderLevel || 5) ? 'var(--accent-red)' : 'var(--accent-emerald)', fontWeight: 600 }}>{v}</span> },
                                    { key: 'reorderLevel', label: 'Reorder Level' },
                                ]} data={inventory} />
                            </div>
                        </>
                    )}
                </>
            )}

            <Modal open={showAddPart} onClose={() => setShowAddPart(false)} title="Add Spare Part" footer={
                <><button className="btn btn-secondary" onClick={() => setShowAddPart(false)}>Cancel</button><button className="btn btn-primary" onClick={handleAddPart}>Add Part</button></>
            }>
                <div className="form-row">
                    <div className="form-group"><label className="form-label">SKU</label><input className="form-input" value={partForm.sku} onChange={e => setPartForm({ ...partForm, sku: e.target.value })} placeholder="SKU-1001" /></div>
                    <div className="form-group"><label className="form-label">Name</label><input className="form-input" value={partForm.name} onChange={e => setPartForm({ ...partForm, name: e.target.value })} placeholder="SSD 256GB" /></div>
                </div>
                <div className="form-row">
                    <div className="form-group"><label className="form-label">Cost Price (₹)</label><input className="form-input" type="number" value={partForm.costPrice} onChange={e => setPartForm({ ...partForm, costPrice: e.target.value })} /></div>
                    <div className="form-group"><label className="form-label">Retail Price (₹)</label><input className="form-input" type="number" value={partForm.retailPrice} onChange={e => setPartForm({ ...partForm, retailPrice: e.target.value })} /></div>
                </div>
            </Modal>
        </div>
    );
}
