import { useState, useEffect, useMemo } from 'react';
import { Plus, Search, DollarSign, CheckCircle, XCircle } from 'lucide-react';
import DataTable from '../../components/DataTable';
import StatusBadge from '../../components/StatusBadge';
import Modal from '../../components/Modal';
import PermissionGate from '../../components/PermissionGate';
import { INVOICE_STATUS, ESTIMATE_STATUS } from '../../utils/constants';
import { formatCurrency, formatDateTime, shortId } from '../../utils/formatters';
import { usePermittedTabs } from '../../hooks/usePermissions';
import { useMaskedColumns } from '../../hooks/useMaskedColumns';
import { useActionPermission } from '../../hooks/useActionPermission';
import { invoiceApi, estimateApi } from '../../api/invoices';
import toast from 'react-hot-toast';

const SAMPLE_INV = Array.from({ length: 15 }, (_, i) => ({
    id: `inv-${i}`, ticketId: `t-${1000 + i}`, amount: [4500, 8200, 15000, 3200, 7800, 2100, 12000, 5600, 9400, 6300][i % 10],
    tax: [810, 1476, 2700, 576, 1404, 378, 2160, 1008, 1692, 1134][i % 10],
    status: ['DRAFT', 'SENT', 'PAID', 'PAID', 'OVERDUE'][i % 5],
    createdAt: new Date(Date.now() - i * 86400000).toISOString(),
}));
const SAMPLE_EST = Array.from({ length: 10 }, (_, i) => ({
    id: `est-${i}`, ticketId: `t-${1000 + i}`, laborCost: [2000, 3500, 5000, 1500][i % 4], partsCost: [1500, 4000, 8000, 1200][i % 4],
    status: ['PENDING', 'APPROVED', 'REJECTED', 'PENDING', 'APPROVED'][i % 5],
    workDescription: ['Thermal paste replacement', 'Screen replacement', 'Motherboard repair', 'Battery replacement'][i % 4],
    createdAt: new Date(Date.now() - i * 86400000).toISOString(),
}));

export default function BillingPage() {
    const permittedTabs = usePermittedTabs('billing');
    const availableTabs = permittedTabs || ['invoices', 'estimates'];
    const [tab, setTab] = useState(availableTabs[0] || 'invoices');
    const [invoices, setInvoices] = useState([]);
    const [estimates, setEstimates] = useState([]);
    const [loading, setLoading] = useState(false);
    const { can: canEstimate } = useActionPermission('estimates');

    useEffect(() => {
        setInvoices(SAMPLE_INV);
        setEstimates(SAMPLE_EST);
    }, []);

    // Ensure tab stays valid when permissions change
    useEffect(() => {
        if (!availableTabs.includes(tab)) setTab(availableTabs[0] || 'invoices');
    }, [availableTabs]);

    const handleApprove = async (id) => {
        try { await estimateApi.approve(id); toast.success('Estimate approved'); } catch { toast.error('Failed'); }
    };
    const handleReject = async (id) => {
        try { await estimateApi.reject(id); toast.success('Estimate rejected'); } catch { toast.error('Failed'); }
    };

    // ── Invoice columns (permission-filtered) ──
    const allInvoiceCols = useMemo(() => [
        { key: 'id', label: 'Invoice', render: v => <span style={{ fontFamily: 'monospace', color: 'var(--accent-blue)' }}>{shortId(v)}</span> },
        { key: 'ticketId', label: 'Ticket', render: v => <span style={{ fontFamily: 'monospace' }}>{shortId(v)}</span> },
        { key: 'amount', label: 'Amount', render: v => <b>{formatCurrency(v)}</b> },
        { key: 'tax', label: 'GST', permKey: 'tax', render: v => formatCurrency(v) },
        { key: 'amount', label: 'Total', permKey: 'total', render: (_, row) => <b style={{ color: 'var(--accent-emerald)' }}>{formatCurrency((row.amount || 0) + (row.tax || 0))}</b> },
        { key: 'status', label: 'Status', render: v => <StatusBadge status={v} statusMap={INVOICE_STATUS} /> },
        { key: 'createdAt', label: 'Date', render: v => formatDateTime(v) },
    ], []);
    const invoiceCols = useMaskedColumns('invoices', allInvoiceCols);

    // ── Estimate columns (permission-filtered) ──
    const allEstimateCols = useMemo(() => [
        { key: 'id', label: 'Estimate', render: v => <span style={{ fontFamily: 'monospace', color: 'var(--accent-purple)' }}>{shortId(v)}</span> },
        { key: 'workDescription', label: 'Work', render: v => <span className="truncate" style={{ maxWidth: 180, display: 'inline-block' }}>{v}</span> },
        { key: 'laborCost', label: 'Labor', permKey: 'laborCost', render: v => formatCurrency(v) },
        { key: 'partsCost', label: 'Parts', permKey: 'partsCost', render: v => formatCurrency(v) },
        { key: 'laborCost', label: 'Total', permKey: 'total', render: (_, row) => <b>{formatCurrency((row.laborCost || 0) + (row.partsCost || 0))}</b> },
        { key: 'status', label: 'Status', render: v => <StatusBadge status={v} statusMap={ESTIMATE_STATUS} /> },
        {
            key: 'id', label: 'Actions', permKey: 'actions', sortable: false, render: (_, row) => row.status === 'PENDING' ? (
                <div className="table-actions">
                    {canEstimate('approve') && <button className="btn btn-sm" style={{ color: 'var(--accent-emerald)', background: 'var(--accent-emerald-glow)' }} onClick={() => handleApprove(row.id)}><CheckCircle size={14} /></button>}
                    {canEstimate('reject') && <button className="btn btn-sm" style={{ color: 'var(--accent-red)', background: 'var(--accent-red-glow)' }} onClick={() => handleReject(row.id)}><XCircle size={14} /></button>}
                </div>
            ) : null
        },
    ], []);
    const estimateCols = useMaskedColumns('estimates', allEstimateCols);

    return (
        <div className="slide-in">
            <div className="page-header"><h1>{tab === 'invoices' ? 'Invoices' : 'Estimates'}</h1></div>
            <div className="tab-bar">
                {availableTabs.includes('invoices') && (
                    <button className={`tab-item ${tab === 'invoices' ? 'active' : ''}`} onClick={() => setTab('invoices')}>Invoices</button>
                )}
                {availableTabs.includes('estimates') && (
                    <button className={`tab-item ${tab === 'estimates' ? 'active' : ''}`} onClick={() => setTab('estimates')}>Estimates</button>
                )}
            </div>

            {tab === 'invoices' && (
                <div className="card">
                    <DataTable columns={invoiceCols} data={invoices} />
                </div>
            )}

            {tab === 'estimates' && (
                <div className="card">
                    <DataTable columns={estimateCols} data={estimates} />
                </div>
            )}
        </div>
    );
}
