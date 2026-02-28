import { NavLink, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { NAV_SECTIONS } from '../utils/constants';
import {
    LayoutDashboard, Ticket, Users, Building2, Wrench, Receipt, FileText,
    Stethoscope, Package, Store, ShieldCheck, Bell, FlaskConical, UserCog,
    ChevronLeft, ChevronRight, Zap
} from 'lucide-react';

const ICON_MAP = {
    LayoutDashboard, Ticket, Users, Building2, Wrench, Receipt, FileText,
    Stethoscope, Package, Store, ShieldCheck, Bell, FlaskConical, UserCog,
};

export default function Sidebar({ collapsed, onToggle }) {
    const { hasAccess, user } = useAuth();
    const location = useLocation();

    return (
        <nav className={`sidebar ${collapsed ? 'collapsed' : ''}`}>
            <div className="sidebar-logo">
                <div className="logo-icon"><Zap size={20} /></div>
                <span className="logo-text">RepairBro</span>
            </div>

            <div style={{ flex: 1, overflowY: 'auto', paddingTop: 8 }}>
                {NAV_SECTIONS.map((section) => {
                    const visibleItems = section.items.filter((item) => hasAccess(item.key));
                    if (visibleItems.length === 0) return null;

                    return (
                        <div className="nav-section" key={section.title}>
                            <div className="nav-section-title">{section.title}</div>
                            {visibleItems.map((item) => {
                                const Icon = ICON_MAP[item.icon] || LayoutDashboard;
                                const isActive = item.path === '/'
                                    ? location.pathname === '/'
                                    : location.pathname.startsWith(item.path);

                                return (
                                    <NavLink
                                        key={item.path}
                                        to={item.path}
                                        className={`nav-item ${isActive ? 'active' : ''}`}
                                        title={collapsed ? item.label : undefined}
                                    >
                                        <Icon size={20} className="nav-icon" />
                                        <span className="nav-label">{item.label}</span>
                                    </NavLink>
                                );
                            })}
                        </div>
                    );
                })}
            </div>

            <div className="sidebar-toggle" onClick={onToggle}>
                {collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
                {!collapsed && <span className="nav-label">Collapse</span>}
            </div>
        </nav>
    );
}
