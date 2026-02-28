import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Ticket, DollarSign, ShieldCheck, Wrench, Users, Package, AlertTriangle, TrendingUp } from 'lucide-react';
import { Line, Doughnut } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, ArcElement, Tooltip, Legend, Filler } from 'chart.js';
import KpiCard from '../components/KpiCard';
import DataTable from '../components/DataTable';
import StatusBadge from '../components/StatusBadge';
import { TICKET_STATUS } from '../utils/constants';
import { formatDateTime, formatCurrencyShort, shortId } from '../utils/formatters';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, ArcElement, Tooltip, Legend, Filler);

// Build mock data for dashboard demo  
const MOCK_TICKETS = Array.from({ length: 25 }, (_, i) => ({
    id: `ticket-${String(i + 1).padStart(4, '0')}`,
    customerName: ['Rahul Sharma', 'Priya Patel', 'Amit Kumar', 'Sneha Reddy', 'Vikram Singh'][i % 5],
    deviceType: ['LAPTOP', 'DESKTOP', 'DRONE', 'TABLET', 'PHONE'][i % 5],
    deviceModel: ['Dell XPS 15', 'iMac 24"', 'DJI Mini 3', 'iPad Pro', 'iPhone 15'][i % 5],
    status: ['OPEN', 'DIAGNOSED', 'IN_REPAIR', 'QA', 'COMPLETED'][i % 5],
    priority: ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'][i % 4],
    branchName: ['Mumbai Central', 'Delhi NCR', 'Bangalore HSR'][i % 3],
    createdAt: new Date(Date.now() - i * 3600000 * 4).toISOString(),
}));

const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
        x: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { color: '#64748b', font: { size: 11 } } },
        y: { grid: { color: 'rgba(255,255,255,0.04)' }, ticks: { color: '#64748b', font: { size: 11 } } },
    },
};

export default function Dashboard() {
    const navigate = useNavigate();

    const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    const revenueData = {
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

    const statusCounts = {};
    MOCK_TICKETS.forEach(t => { statusCounts[t.status] = (statusCounts[t.status] || 0) + 1; });
    const pipelineData = {
        labels: Object.keys(statusCounts).map(s => TICKET_STATUS[s]?.label || s),
        datasets: [{
            data: Object.values(statusCounts),
            backgroundColor: Object.keys(statusCounts).map(s => TICKET_STATUS[s]?.color || '#64748b'),
            borderWidth: 0,
        }],
    };

    const ticketColumns = [
        { key: 'id', label: 'Ticket ID', render: (v) => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
        { key: 'customerName', label: 'Customer' },
        { key: 'deviceModel', label: 'Device' },
        { key: 'status', label: 'Status', render: (v) => <StatusBadge status={v} statusMap={TICKET_STATUS} /> },
        { key: 'branchName', label: 'Branch' },
        { key: 'createdAt', label: 'Created', render: (v) => formatDateTime(v) },
    ];

    return (
        <div className="slide-in">
            <div className="kpi-grid">
                <KpiCard icon={Ticket} label="Total Tickets" value="1,247" trend={12} accent="blue" />
                <KpiCard icon={Wrench} label="Active Repairs" value="89" trend={-3} accent="amber" />
                <KpiCard icon={DollarSign} label="Revenue Today" value={formatCurrencyShort(240000)} trend={8} accent="emerald" />
                <KpiCard icon={ShieldCheck} label="SLA Score" value="96.4%" trend={2} accent="purple" />
                <KpiCard icon={Users} label="Technicians Online" value="34" trend={5} accent="cyan" />
                <KpiCard icon={Package} label="Low Stock Alerts" value="7" trend={-2} accent="red" />
            </div>

            <div className="chart-grid">
                <div className="card">
                    <div className="card-header">
                        <h3 className="card-title">Revenue Trend (This Week)</h3>
                    </div>
                    <div style={{ height: 260 }}>
                        <Line data={revenueData} options={chartOptions} />
                    </div>
                </div>
                <div className="card">
                    <div className="card-header">
                        <h3 className="card-title">Ticket Pipeline</h3>
                    </div>
                    <div style={{ height: 260, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <Doughnut
                            data={pipelineData}
                            options={{
                                responsive: true,
                                maintainAspectRatio: false,
                                cutout: '65%',
                                plugins: {
                                    legend: { position: 'right', labels: { color: '#94a3b8', font: { size: 12 }, padding: 14, usePointStyle: true, pointStyle: 'circle' } },
                                },
                            }}
                        />
                    </div>
                </div>
            </div>

            <div className="card">
                <div className="card-header">
                    <h3 className="card-title">Recent Tickets</h3>
                    <button className="btn btn-secondary btn-sm" onClick={() => navigate('/tickets')}>View All</button>
                </div>
                <DataTable columns={ticketColumns} data={MOCK_TICKETS.slice(0, 10)} onRowClick={(row) => navigate(`/tickets/${row.id}`)} />
            </div>
        </div>
    );
}
