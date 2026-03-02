import { useState, useEffect } from 'react';
import { UserCog } from 'lucide-react';
import DataTable from '../../components/DataTable';
import { formatDateTime, shortId } from '../../utils/formatters';
import { authApi } from '../../api/auth';
import { usePermittedTabs } from '../../hooks/usePermissions';

const SAMPLE = Array.from({ length: 10 }, (_, i) => ({
    id: `u-${i}`, name: ['Admin User', 'Rajesh Manager', 'Vikram Tech', 'Priya Tech', 'Sunil Franchise', 'Amit Customer', 'Sneha Admin', 'Deepa Manager', 'Rahul Tech', 'Anita Customer'][i],
    email: `user${i}@repairbro.com`, role: ['ADMIN', 'BRANCH_MANAGER', 'TECHNICIAN', 'TECHNICIAN', 'FRANCHISEE', 'CUSTOMER', 'ADMIN', 'BRANCH_MANAGER', 'TECHNICIAN', 'CUSTOMER'][i],
    createdAt: new Date(Date.now() - i * 864000000).toISOString(),
}));

export default function UserManagement() {
    const permittedTabs = usePermittedTabs('users');
    const availableTabs = permittedTabs || ['userList'];
    const [tab, setTab] = useState('userList');
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => { load(); }, []);
    useEffect(() => {
        if (!availableTabs.includes(tab)) setTab(availableTabs[0] || 'userList');
    }, [availableTabs]);
    const load = async () => {
        setLoading(true);
        try { const d = await authApi.getAllUsers(); setUsers(Array.isArray(d) ? d : []); }
        catch { setUsers(SAMPLE); } finally { setLoading(false); }
    };

    const roleColors = { ADMIN: '#ef4444', BRANCH_MANAGER: '#8b5cf6', TECHNICIAN: '#3b82f6', FRANCHISEE: '#f59e0b', CUSTOMER: '#10b981' };

    return (
        <div className="slide-in">
            <div className="page-header"><h1>User Management</h1></div>
            {availableTabs.length > 1 && (
                <div className="tab-bar">
                    {availableTabs.includes('userList') && <button className={`tab-item ${tab === 'userList' ? 'active' : ''}`} onClick={() => setTab('userList')}>Users</button>}
                    {availableTabs.includes('auditLog') && <button className={`tab-item ${tab === 'auditLog' ? 'active' : ''}`} onClick={() => setTab('auditLog')}>Audit Log</button>}
                    {availableTabs.includes('loginHistory') && <button className={`tab-item ${tab === 'loginHistory' ? 'active' : ''}`} onClick={() => setTab('loginHistory')}>Login History</button>}
                </div>
            )}
            {tab === 'userList' && (
                <div className="card">
                    {loading ? <div className="loading-spinner"><div className="spinner" /></div> : (
                        <DataTable columns={[
                            { key: 'id', label: 'ID', render: v => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
                            { key: 'name', label: 'Name', render: v => <b>{v}</b> },
                            { key: 'email', label: 'Email' },
                            { key: 'role', label: 'Role', render: v => <span className="status-badge" style={{ color: roleColors[v] || '#94a3b8', background: `${roleColors[v] || '#94a3b8'}18`, border: `1px solid ${roleColors[v] || '#94a3b8'}30` }}>{v}</span> },
                            { key: 'createdAt', label: 'Joined', render: v => formatDateTime(v) },
                        ]} data={users} />
                    )}
                </div>
            )}
            {tab === 'auditLog' && (
                <div className="card"><div className="empty-state"><div className="empty-state-title">Audit Log</div><p className="text-muted">Coming soon — view all permission changes and critical actions.</p></div></div>
            )}
            {tab === 'loginHistory' && (
                <div className="card"><div className="empty-state"><div className="empty-state-title">Login History</div><p className="text-muted">Coming soon — view all login and logout events.</p></div></div>
            )}
        </div>
    );
}
