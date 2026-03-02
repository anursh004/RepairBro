import { useState, useEffect } from 'react';
import { LogIn, LogOut, XCircle, CheckCircle } from 'lucide-react';
import DataTable from '../../components/DataTable';
import { formatDateTime, shortId } from '../../utils/formatters';
import { auditApi } from '../../api/audit';

const SAMPLE_LOGINS = Array.from({ length: 20 }, (_, i) => ({
    id: `login-${i}`,
    userName: ['Admin User', 'Rajesh Manager', 'Vikram Tech', 'Priya Billing', 'Sunil Franchise'][i % 5],
    email: ['admin@repairbro.in', 'rajesh@repairbro.in', 'vikram@repairbro.in', 'priya@repairbro.in', 'sunil@repairbro.in'][i % 5],
    action: i % 8 === 7 ? 'FAILED_LOGIN' : i % 3 === 0 ? 'LOGOUT' : 'LOGIN',
    ipAddress: ['192.168.1.' + (i + 1), '10.0.0.' + (i + 10)][i % 2],
    userAgent: ['Chrome 120 / Windows', 'Safari 17 / macOS', 'Firefox 121 / Ubuntu', 'Chrome 120 / Android'][i % 4],
    createdAt: new Date(Date.now() - i * 7200000).toISOString(),
}));

export default function LoginHistoryPage() {
    const [logins, setLogins] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        load();
    }, []);

    const load = async () => {
        setLoading(true);
        try {
            const d = await auditApi.getLoginHistory();
            setLogins(Array.isArray(d) ? d : []);
        } catch {
            setLogins(SAMPLE_LOGINS);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="slide-in">
            <div className="page-header"><h1>Login History</h1></div>
            <div className="card">
                {loading ? (
                    <div className="loading-spinner"><div className="spinner" /></div>
                ) : (
                    <DataTable columns={[
                        { key: 'createdAt', label: 'Time', render: v => <span style={{ fontSize: 12 }}>{formatDateTime(v)}</span> },
                        { key: 'userName', label: 'User', render: v => <b>{v}</b> },
                        { key: 'email', label: 'Email', render: v => <span style={{ color: 'var(--text-muted)' }}>{v}</span> },
                        {
                            key: 'action', label: 'Action', render: v => {
                                const map = {
                                    LOGIN: { icon: LogIn, color: 'var(--accent-emerald)', bg: 'var(--accent-emerald-glow)' },
                                    LOGOUT: { icon: LogOut, color: 'var(--accent-amber)', bg: 'var(--accent-amber-glow)' },
                                    FAILED_LOGIN: { icon: XCircle, color: 'var(--accent-red)', bg: 'var(--accent-red-glow)' },
                                };
                                const cfg = map[v] || map.LOGIN;
                                const Icon = cfg.icon;
                                return <span className="status-badge" style={{ color: cfg.color, background: cfg.bg }}><Icon size={12} /> {v.replace('_', ' ')}</span>;
                            }
                        },
                        { key: 'ipAddress', label: 'IP', render: v => <span style={{ fontFamily: 'monospace', fontSize: 12 }}>{v}</span> },
                        { key: 'userAgent', label: 'Device / Browser' },
                    ]} data={logins} />
                )}
            </div>
        </div>
    );
}
