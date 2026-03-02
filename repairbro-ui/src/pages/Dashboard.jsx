import { useNavigate } from 'react-router-dom';
import {
    Ticket, DollarSign, ShieldCheck, Wrench, Users, Package,
    AlertTriangle, TrendingUp, Receipt, Clock, FileText, Store, CheckCircle,
} from 'lucide-react';
import { Line, Doughnut, Bar } from 'react-chartjs-2';
import {
    Chart as ChartJS, CategoryScale, LinearScale, PointElement,
    LineElement, ArcElement, BarElement, Tooltip, Legend, Filler,
} from 'chart.js';
import KpiCard from '../components/KpiCard';
import DataTable from '../components/DataTable';
import StatusBadge from '../components/StatusBadge';
import { TICKET_STATUS, INVOICE_STATUS } from '../utils/constants';
import { formatDateTime, formatCurrencyShort, formatCurrency, shortId } from '../utils/formatters';
import { useDashboardConfig } from '../hooks/useDashboardConfig';

ChartJS.register(
    CategoryScale, LinearScale, PointElement, LineElement,
    ArcElement, BarElement, Tooltip, Legend, Filler,
);

// ── KPI Registry ──────────────────────────────────────────────
const KPI_REGISTRY = {
    totalTickets:      { icon: Ticket, label: 'Total Tickets', value: '1,247', trend: 12, accent: 'blue' },
    activeRepairs:     { icon: Wrench, label: 'Active Repairs', value: '89', trend: -3, accent: 'amber' },
    revenueToday:      { icon: DollarSign, label: 'Revenue Today', value: formatCurrencyShort(240000), trend: 8, accent: 'emerald' },
    slaScore:          { icon: ShieldCheck, label: 'SLA Score', value: '96.4%', trend: 2, accent: 'purple' },
    techniciansOnline: { icon: Users, label: 'Techs Online', value: '34', trend: 5, accent: 'cyan' },
    lowStockAlerts:    { icon: AlertTriangle, label: 'Low Stock', value: '7', trend: -2, accent: 'red' },
    monthlyRevenue:    { icon: TrendingUp, label: 'Monthly Revenue', value: formatCurrencyShort(5200000), trend: 15, accent: 'emerald' },
    branchCount:       { icon: Store, label: 'Branches', value: '12', accent: 'purple' },
    myTickets:         { icon: Ticket, label: 'My Tickets', value: '8', trend: 1, accent: 'blue' },
    pendingDiagnosis:  { icon: Clock, label: 'Pending Diagnosis', value: '3', accent: 'amber' },
    awaitingParts:     { icon: Package, label: 'Awaiting Parts', value: '2', accent: 'red' },
    completedToday:    { icon: CheckCircle, label: 'Completed Today', value: '5', trend: 25, accent: 'emerald' },
    pendingInvoices:   { icon: Receipt, label: 'Pending Invoices', value: '23', trend: -5, accent: 'amber' },
    overdueInvoices:   { icon: AlertTriangle, label: 'Overdue', value: '4', accent: 'red' },
    pendingOrders:     { icon: FileText, label: 'Pending Orders', value: '6', accent: 'amber' },
    totalParts:        { icon: Package, label: 'Total Parts', value: '342', accent: 'blue' },
    reservedParts:     { icon: Package, label: 'Reserved', value: '18', accent: 'purple' },
    myRevenue:         { icon: DollarSign, label: 'My Revenue', value: formatCurrencyShort(890000), trend: 10, accent: 'emerald' },
    royaltyDue:        { icon: Receipt, label: 'Royalty Due', value: formatCurrencyShort(62300), accent: 'amber' },
    activeRepair:      { icon: Wrench, label: 'Active Repair', value: '1', accent: 'blue' },
};

// ── Chart Options ─────────────────────────────────────────────
const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
        x: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { color: '#64748b', font: { size: 11 } } },
        y: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { color: '#64748b', font: { size: 11 } } },
    },
};
const doughnutOpts = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '65%',
    plugins: {
        legend: {
            position: 'right',
            labels: { color: '#94a3b8', font: { size: 12 }, padding: 14, usePointStyle: true, pointStyle: 'circle' },
        },
    },
};

// ── Mock Data ─────────────────────────────────────────────────
const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

const REVENUE_DATA = {
    labels: days,
    datasets: [{
        data: [185000, 210000, 195000, 240000, 280000, 220000, 175000],
        borderColor: '#3b82f6',
        backgroundColor: 'rgba(59,130,246,0.08)',
        fill: true,
        tension: 0.4,
        pointRadius: 4,
        pointBackgroundColor: '#3b82f6',
    }],
};

const PIPELINE_DATA = {
    labels: ['Open', 'Diagnosed', 'In Repair', 'QA', 'Completed'],
    datasets: [{
        data: [18, 12, 25, 8, 37],
        backgroundColor: ['#3b82f6', '#8b5cf6', '#f97316', '#06b6d4', '#10b981'],
        borderWidth: 0,
    }],
};

const BRANCH_DATA = {
    labels: ['Mumbai', 'Delhi', 'Bangalore', 'Pune', 'Chennai'],
    datasets: [{
        data: [420000, 380000, 350000, 290000, 260000],
        backgroundColor: 'rgba(59,130,246,0.6)',
        borderRadius: 6,
    }],
};

const SLA_DATA = {
    labels: ['On Track', 'At Risk', 'Breached'],
    datasets: [{
        data: [85, 10, 5],
        backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
        borderWidth: 0,
    }],
};

const PAYMENT_DATA = {
    labels: ['Paid', 'Pending', 'Overdue'],
    datasets: [{
        data: [68, 23, 9],
        backgroundColor: ['#10b981', '#f59e0b', '#ef4444'],
        borderWidth: 0,
    }],
};

const STOCK_DATA = {
    labels: ['Thermal Paste', 'SSD 256GB', 'RAM 8GB', 'Display Cable', 'Battery', 'Fan', 'Keyboard'],
    datasets: [{
        data: [45, 12, 8, 22, 5, 15, 30],
        backgroundColor: 'rgba(245,158,11,0.6)',
        borderRadius: 6,
    }],
};

const MOCK_TICKETS = Array.from({ length: 10 }, (_, i) => ({
    id: `ticket-${String(i + 1).padStart(4, '0')}`,
    customerName: ['Rahul Sharma', 'Priya Patel', 'Amit Kumar', 'Sneha Reddy', 'Vikram Singh'][i % 5],
    deviceModel: ['Dell XPS 15', 'iMac 24"', 'DJI Mini 3', 'iPad Pro', 'iPhone 15'][i % 5],
    status: ['OPEN', 'DIAGNOSED', 'IN_REPAIR', 'QA', 'COMPLETED'][i % 5],
    branchName: ['Mumbai Central', 'Delhi NCR', 'Bangalore HSR'][i % 3],
    createdAt: new Date(Date.now() - i * 3600000 * 4).toISOString(),
}));

const MOCK_INVOICES = Array.from({ length: 8 }, (_, i) => ({
    id: `inv-${i}`,
    ticketId: `t-${1000 + i}`,
    amount: [4500, 8200, 15000, 3200, 7800, 2100, 12000, 5600][i],
    status: ['PAID', 'SENT', 'PAID', 'OVERDUE', 'PAID', 'SENT', 'PAID', 'DRAFT'][i],
    createdAt: new Date(Date.now() - i * 86400000).toISOString(),
}));

const MOCK_LOW_STOCK = [
    { id: 'ls1', partName: 'Battery Pack', qty: 2, reorderLevel: 5, branchName: 'Mumbai Central' },
    { id: 'ls2', partName: 'SSD 256GB', qty: 3, reorderLevel: 10, branchName: 'Delhi NCR' },
    { id: 'ls3', partName: 'RAM 8GB DDR4', qty: 1, reorderLevel: 5, branchName: 'Bangalore HSR' },
    { id: 'ls4', partName: 'Display Cable', qty: 4, reorderLevel: 8, branchName: 'Pune' },
];

const MOCK_TOP_TECHS = [
    { id: 't1', name: 'Vikram Patel', resolved: 45, rating: 4.8, specialization: 'LAPTOP' },
    { id: 't2', name: 'Priya Singh', resolved: 42, rating: 4.9, specialization: 'DESKTOP' },
    { id: 't3', name: 'Rahul Dev', resolved: 38, rating: 4.7, specialization: 'DRONE' },
    { id: 't4', name: 'Sneha Iyer', resolved: 35, rating: 4.6, specialization: 'LAPTOP' },
];

// ── Chart Registry ────────────────────────────────────────────
const CHART_REGISTRY = {
    revenueTrend: {
        title: 'Revenue Trend (This Week)',
        component: () => (
            <div style={{ height: 260 }}>
                <Line data={REVENUE_DATA} options={chartOptions} />
            </div>
        ),
    },
    ticketPipeline: {
        title: 'Ticket Pipeline',
        component: () => (
            <div style={{ height: 260, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Doughnut data={PIPELINE_DATA} options={doughnutOpts} />
            </div>
        ),
    },
    branchComparison: {
        title: 'Revenue by Branch',
        component: () => (
            <div style={{ height: 260 }}>
                <Bar data={BRANCH_DATA} options={{ ...chartOptions, plugins: { legend: { display: false } } }} />
            </div>
        ),
    },
    slaOverview: {
        title: 'SLA Overview',
        component: () => (
            <div style={{ height: 260, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Doughnut data={SLA_DATA} options={doughnutOpts} />
            </div>
        ),
    },
    paymentStatus: {
        title: 'Payment Status',
        component: () => (
            <div style={{ height: 260, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Doughnut data={PAYMENT_DATA} options={doughnutOpts} />
            </div>
        ),
    },
    stockLevels: {
        title: 'Stock Levels',
        component: () => (
            <div style={{ height: 260 }}>
                <Bar data={STOCK_DATA} options={{ ...chartOptions, plugins: { legend: { display: false } } }} />
            </div>
        ),
    },
};

// ── Table Registry ────────────────────────────────────────────
const TABLE_REGISTRY = {
    recentTickets: {
        title: 'Recent Tickets',
        link: '/tickets',
        cols: [
            { key: 'id', label: 'ID', render: (v) => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
            { key: 'customerName', label: 'Customer' },
            { key: 'deviceModel', label: 'Device' },
            { key: 'status', label: 'Status', render: (v) => <StatusBadge status={v} statusMap={TICKET_STATUS} /> },
            { key: 'branchName', label: 'Branch' },
            { key: 'createdAt', label: 'Created', render: (v) => formatDateTime(v) },
        ],
        data: MOCK_TICKETS,
    },
    myAssignedTickets: {
        title: 'My Assigned Tickets',
        link: '/tickets',
        cols: [
            { key: 'id', label: 'ID', render: (v) => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
            { key: 'deviceModel', label: 'Device' },
            { key: 'status', label: 'Status', render: (v) => <StatusBadge status={v} statusMap={TICKET_STATUS} /> },
            { key: 'createdAt', label: 'Created', render: (v) => formatDateTime(v) },
        ],
        data: MOCK_TICKETS.slice(0, 5),
    },
    myTickets: {
        title: 'My Tickets',
        link: '/tickets',
        cols: [
            { key: 'id', label: 'ID', render: (v) => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
            { key: 'deviceModel', label: 'Device' },
            { key: 'status', label: 'Status', render: (v) => <StatusBadge status={v} statusMap={TICKET_STATUS} /> },
            { key: 'createdAt', label: 'Created', render: (v) => formatDateTime(v) },
        ],
        data: MOCK_TICKETS.slice(0, 3),
    },
    topTechnicians: {
        title: 'Top Technicians',
        link: '/technicians',
        cols: [
            { key: 'name', label: 'Name', render: (v) => <b>{v}</b> },
            { key: 'resolved', label: 'Resolved', render: (v) => <span style={{ color: 'var(--accent-emerald)', fontWeight: 600 }}>{v}</span> },
            { key: 'rating', label: 'Rating', render: (v) => <span style={{ color: 'var(--accent-amber)' }}>{v} ★</span> },
            { key: 'specialization', label: 'Specialization' },
        ],
        data: MOCK_TOP_TECHS,
    },
    recentInvoices: {
        title: 'Recent Invoices',
        link: '/invoices',
        cols: [
            { key: 'id', label: 'Invoice', render: (v) => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
            { key: 'ticketId', label: 'Ticket', render: (v) => <span style={{ fontFamily: 'monospace' }}>{shortId(v)}</span> },
            { key: 'amount', label: 'Amount', render: (v) => <b>{formatCurrency(v)}</b> },
            { key: 'status', label: 'Status', render: (v) => <StatusBadge status={v} statusMap={INVOICE_STATUS} /> },
            { key: 'createdAt', label: 'Date', render: (v) => formatDateTime(v) },
        ],
        data: MOCK_INVOICES,
    },
    lowStockItems: {
        title: 'Low Stock Items',
        link: '/inventory',
        cols: [
            { key: 'partName', label: 'Part', render: (v) => <b>{v}</b> },
            { key: 'qty', label: 'Qty', render: (v) => <span style={{ color: 'var(--accent-red)', fontWeight: 600 }}>{v}</span> },
            { key: 'reorderLevel', label: 'Reorder At' },
            { key: 'branchName', label: 'Branch' },
        ],
        data: MOCK_LOW_STOCK,
    },
};

// ── Level Labels ──────────────────────────────────────────────
const LEVEL_LABELS = {
    EXECUTIVE: 'Executive Dashboard',
    MANAGEMENT: 'Management Dashboard',
    OPERATIONAL: 'My Workspace',
    FINANCE: 'Finance Dashboard',
    LOGISTICS: 'Inventory Dashboard',
    PARTNER: 'Franchise Dashboard',
    EXTERNAL: 'My Repairs',
};

// ── Dashboard Component ───────────────────────────────────────
export default function Dashboard() {
    const navigate = useNavigate();
    const { kpis, charts, tables, level } = useDashboardConfig();

    return (
        <div className="slide-in">
            <div className="page-header" style={{ marginBottom: 8 }}>
                <h1>{LEVEL_LABELS[level] || 'Dashboard'}</h1>
                <span
                    className="status-badge"
                    style={{
                        color: 'var(--accent-blue)',
                        background: 'var(--accent-blue-glow)',
                        fontSize: 12,
                        fontWeight: 500,
                    }}
                >
                    {level}
                </span>
            </div>

            {/* KPI Grid */}
            <div className="kpi-grid">
                {kpis.map((key) => {
                    const kpi = KPI_REGISTRY[key];
                    if (!kpi) return null;
                    return (
                        <KpiCard
                            key={key}
                            icon={kpi.icon}
                            label={kpi.label}
                            value={kpi.value}
                            trend={kpi.trend}
                            accent={kpi.accent}
                        />
                    );
                })}
            </div>

            {/* Charts Grid */}
            {charts.length > 0 && (
                <div className="chart-grid">
                    {charts.map((key) => {
                        const chart = CHART_REGISTRY[key];
                        if (!chart) return null;
                        return (
                            <div key={key} className="card">
                                <div className="card-header">
                                    <h3 className="card-title">{chart.title}</h3>
                                </div>
                                {chart.component()}
                            </div>
                        );
                    })}
                </div>
            )}

            {/* Tables */}
            {tables.map((key) => {
                const table = TABLE_REGISTRY[key];
                if (!table) return null;
                return (
                    <div key={key} className="card" style={{ marginBottom: 20 }}>
                        <div className="card-header">
                            <h3 className="card-title">{table.title}</h3>
                            {table.link && (
                                <button
                                    className="btn btn-secondary btn-sm"
                                    onClick={() => navigate(table.link)}
                                >
                                    View All
                                </button>
                            )}
                        </div>
                        <DataTable
                            columns={table.cols}
                            data={table.data}
                            onRowClick={
                                table.link
                                    ? (row) => navigate(`${table.link}/${row.id}`)
                                    : undefined
                            }
                        />
                    </div>
                );
            })}
        </div>
    );
}
