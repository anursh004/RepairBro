import { useState } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import Sidebar from './Sidebar';
import { useAuth } from '../hooks/useAuth';
import { LogOut, Search, MapPin, Users } from 'lucide-react';

const PAGE_TITLES = {
    '/': 'Dashboard',
    '/tickets': 'Repair Tickets',
    '/customers': 'Customers',
    '/branches': 'Branches',
    '/technicians': 'Technicians',
    '/invoices': 'Invoices',
    '/estimates': 'Estimates',
    '/diagnostics': 'Diagnostics',
    '/inventory': 'Inventory & Parts',
    '/franchises': 'Franchise Hub',
    '/sla': 'SLA Monitor',
    '/notifications': 'Notifications',
    '/simulation': 'Simulation Lab',
    '/users': 'User Management',
    '/audit': 'Audit Log',
    '/audit/logins': 'Login History',
};

export default function Layout() {
    const [collapsed, setCollapsed] = useState(false);
    const { user, logout } = useAuth();
    const location = useLocation();

    const matchedKey = Object.keys(PAGE_TITLES)
        .filter(k => k !== '/')
        .sort((a, b) => b.length - a.length)
        .find(k => location.pathname.startsWith(k));
    const pageTitle = matchedKey
        ? PAGE_TITLES[matchedKey]
        : PAGE_TITLES['/'];

    return (
        <div className="app-layout">
            <Sidebar collapsed={collapsed} onToggle={() => setCollapsed(!collapsed)} />

            <div className={`main-area ${collapsed ? 'collapsed' : ''}`}>
                <header className={`topbar ${collapsed ? 'collapsed' : ''}`}>
                    <div className="topbar-left">
                        <h2 className="page-title">{pageTitle}</h2>
                    </div>
                    <div className="topbar-right">
                        {user?.groups?.length > 0 && (
                            <span className="status-badge" style={{ color: 'var(--accent-purple)', background: 'var(--accent-purple-glow)', fontSize: 11, gap: 4 }}>
                                <Users size={12} />
                                {user.groups[0]}
                                {user.groups.length > 1 && ` +${user.groups.length - 1}`}
                            </span>
                        )}
                        {user?.primaryLocation && (
                            <span className="status-badge" style={{ color: 'var(--accent-cyan)', background: 'var(--accent-cyan-glow)', fontSize: 11, gap: 4 }}>
                                <MapPin size={12} />
                                {typeof user.primaryLocation === 'string' ? user.primaryLocation : (user.primaryLocation.name || user.primaryLocation.city || 'HQ')}
                            </span>
                        )}
                        <div className="user-menu">
                            <div className="user-avatar">
                                {(user?.name || 'A').charAt(0).toUpperCase()}
                            </div>
                            <div className="user-info">
                                <span className="user-name">{user?.name || 'Admin'}</span>
                                <span className="user-role">{user?.groupLevel || user?.role || 'ADMIN'}</span>
                            </div>
                        </div>
                        <button className="btn btn-ghost btn-icon" onClick={logout} title="Logout">
                            <LogOut size={18} />
                        </button>
                    </div>
                </header>

                <main className="page-content fade-in">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
