import { useState, useEffect } from 'react';
import { Plus, Search, Phone } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import { formatDateTime, shortId } from '../../utils/formatters';
import { customerApi } from '../../api/customers';
import { useActionPermission } from '../../hooks/useActionPermission';
import { useMaskedColumns } from '../../hooks/useMaskedColumns';
import PermissionField from '../../components/PermissionField';
import toast from 'react-hot-toast';

const SAMPLE = Array.from({ length: 20 }, (_, i) => ({
    id: `cust-${i}`, name: ['Rahul Sharma', 'Priya Patel', 'Amit Kumar', 'Sneha Reddy', 'Vikram Singh', 'Anita Gupta', 'Suresh Nair', 'Deepa Iyer'][i % 8],
    phone: `+91 ${9876500000 + i}`, email: `user${i}@example.com`, createdAt: new Date(Date.now() - i * 86400000).toISOString(),
}));

export default function CustomerList() {
    const { canCreate } = useActionPermission('customers');
    const [customers, setCustomers] = useState([]);
    const [search, setSearch] = useState('');
    const [loading, setLoading] = useState(false);
    const [showCreate, setShowCreate] = useState(false);
    const [form, setForm] = useState({ name: '', phone: '', email: '' });

    useEffect(() => { load(); }, []);
    const load = async () => {
        setLoading(true);
        try { const d = await customerApi.getAll(); setCustomers(Array.isArray(d) ? d : []); }
        catch { setCustomers(SAMPLE); } finally { setLoading(false); }
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        try { await customerApi.create(form); toast.success('Customer created'); setShowCreate(false); setForm({ name: '', phone: '', email: '' }); load(); }
        catch { toast.error('Failed'); }
    };

    const filtered = customers.filter(c => {
        if (!search) return true;
        const s = search.toLowerCase();
        return c.name?.toLowerCase().includes(s) || c.phone?.includes(s) || c.email?.toLowerCase().includes(s);
    });

    const allColumns = [
        { key: 'id', label: 'ID', render: v => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
        { key: 'name', label: 'Name', render: v => <span style={{ fontWeight: 500 }}>{v}</span> },
        { key: 'phone', label: 'Phone', render: v => <span className="flex items-center gap-8"><Phone size={14} style={{ color: 'var(--text-muted)' }} />{v}</span> },
        { key: 'email', label: 'Email' },
        { key: 'createdAt', label: 'Joined', render: v => formatDateTime(v) },
    ];
    const columns = useMaskedColumns('customers', allColumns);

    return (
        <div className="slide-in">
            <div className="page-header">
                <h1>Customers</h1>
                {canCreate && <button className="btn btn-primary" onClick={() => setShowCreate(true)}><Plus size={16} /> Add Customer</button>}
            </div>
            <div className="filter-bar">
                <div className="search-input"><Search size={16} className="search-icon" /><input placeholder="Search by name, phone, email..." value={search} onChange={(e) => setSearch(e.target.value)} /></div>
            </div>
            <div className="card">
                {loading ? <div className="loading-spinner"><div className="spinner" /></div> : (
                    <DataTable columns={columns} data={filtered} />
                )}
            </div>
            <Modal open={showCreate} onClose={() => setShowCreate(false)} title="Add Customer" footer={
                <><button className="btn btn-secondary" onClick={() => setShowCreate(false)}>Cancel</button><button className="btn btn-primary" onClick={handleCreate}>Create</button></>
            }>
                <div className="form-group"><label className="form-label">Name</label><input className="form-input" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Full name" /></div>
                <div className="form-row">
                    <PermissionField page="customers" field="phone">
                        <div className="form-group"><label className="form-label">Phone</label><input className="form-input" value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} placeholder="+91 98765 43210" /></div>
                    </PermissionField>
                    <PermissionField page="customers" field="email">
                        <div className="form-group"><label className="form-label">Email</label><input className="form-input" type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="user@example.com" /></div>
                    </PermissionField>
                </div>
            </Modal>
        </div>
    );
}
