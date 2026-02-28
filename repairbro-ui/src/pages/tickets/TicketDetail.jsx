import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, CheckCircle, Circle, Clock, AlertCircle, Wrench, FileText, Bell, UserPlus } from 'lucide-react';
import StatusBadge from '../../components/StatusBadge';
import Modal from '../../components/Modal';
import { TICKET_STATUS, PRIORITIES } from '../../utils/constants';
import { formatDateTime, formatCurrency } from '../../utils/formatters';
import { ticketApi } from '../../api/tickets';
import toast from 'react-hot-toast';

export default function TicketDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [ticket, setTicket] = useState(null);
    const [loading, setLoading] = useState(true);
    const [showStatusModal, setShowStatusModal] = useState(false);
    const [showDiagModal, setShowDiagModal] = useState(false);
    const [newStatus, setNewStatus] = useState('');
    const [diagForm, setDiagForm] = useState({ stepName: '', result: 'PASS', notes: '' });

    useEffect(() => {
        loadTicket();
    }, [id]);

    const loadTicket = async () => {
        setLoading(true);
        try {
            const data = await ticketApi.get(id);
            setTicket(data);
        } catch {
            // sample fallback
            setTicket({
                id, deviceType: 'LAPTOP', deviceModel: 'Dell XPS 15', status: 'IN_REPAIR',
                priority: 'HIGH', symptom: 'Screen flickering and random shutdowns',
                customerName: 'Rahul Sharma', customerPhone: '+91 98765 43210',
                branchName: 'Mumbai Central', technicianName: 'Vikram Patel',
                estimatedCost: 4500, createdAt: new Date(Date.now() - 86400000).toISOString(),
                diagnosisSteps: [
                    { id: '1', stepName: 'Visual Inspection', result: 'PASS', notes: 'No visible damage', createdAt: new Date(Date.now() - 80000000).toISOString() },
                    { id: '2', stepName: 'GPU Stress Test', result: 'FAIL', notes: 'GPU overheating at 95°C — thermal paste dried', createdAt: new Date(Date.now() - 72000000).toISOString() },
                ],
            });
        } finally {
            setLoading(false);
        }
    };

    const handleStatusUpdate = async () => {
        try {
            await ticketApi.updateStatus(id, { status: newStatus });
            toast.success('Status updated');
            setShowStatusModal(false);
            loadTicket();
        } catch { toast.error('Failed to update status'); }
    };

    const handleDiagnosis = async () => {
        try {
            await ticketApi.addDiagnosis(id, diagForm);
            toast.success('Diagnosis step added');
            setShowDiagModal(false);
            setDiagForm({ stepName: '', result: 'PASS', notes: '' });
            loadTicket();
        } catch { toast.error('Failed to add diagnosis'); }
    };

    if (loading) return <div className="loading-spinner"><div className="spinner" /></div>;
    if (!ticket) return <div className="empty-state"><p>Ticket not found</p></div>;

    const steps = ticket.diagnosisSteps || [];
    const statusList = ['OPEN', 'DIAGNOSED', 'AWAITING_PARTS', 'IN_REPAIR', 'QA', 'COMPLETED'];
    const currentIdx = statusList.indexOf(ticket.status);

    return (
        <div className="slide-in">
            <div className="page-header">
                <div className="flex items-center gap-16">
                    <button className="btn btn-ghost btn-icon" onClick={() => navigate('/tickets')}><ArrowLeft size={20} /></button>
                    <div>
                        <h1 style={{ fontSize: 20 }}>Ticket #{(ticket.id || '').substring(0, 8).toUpperCase()}</h1>
                        <span className="text-sm text-muted">Created {formatDateTime(ticket.createdAt)}</span>
                    </div>
                </div>
                <StatusBadge status={ticket.status} statusMap={TICKET_STATUS} />
            </div>

            <div className="detail-grid">
                {/* Left: Ticket Info */}
                <div className="card">
                    <h3 className="card-title" style={{ marginBottom: 20 }}>Ticket Details</h3>
                    <div className="detail-field"><div className="detail-label">Device</div><div className="detail-value">{ticket.deviceType} — {ticket.deviceModel}</div></div>
                    <div className="detail-field"><div className="detail-label">Symptom</div><div className="detail-value">{ticket.symptom}</div></div>
                    <div className="detail-field"><div className="detail-label">Priority</div><div className="detail-value" style={{ color: PRIORITIES[ticket.priority]?.color }}>{ticket.priority}</div></div>
                    <div className="detail-field"><div className="detail-label">Customer</div><div className="detail-value">{ticket.customerName || '—'}</div></div>
                    <div className="detail-field"><div className="detail-label">Phone</div><div className="detail-value">{ticket.customerPhone || '—'}</div></div>
                    <div className="detail-field"><div className="detail-label">Branch</div><div className="detail-value">{ticket.branchName || '—'}</div></div>
                    <div className="detail-field"><div className="detail-label">Technician</div><div className="detail-value">{ticket.technicianName || 'Not assigned'}</div></div>
                    <div className="detail-field"><div className="detail-label">Estimated Cost</div><div className="detail-value">{formatCurrency(ticket.estimatedCost)}</div></div>

                    <div className="action-bar">
                        <button className="btn btn-primary btn-sm" onClick={() => setShowStatusModal(true)}><CheckCircle size={14} /> Update Status</button>
                        <button className="btn btn-secondary btn-sm" onClick={() => setShowDiagModal(true)}><Wrench size={14} /> Add Diagnosis</button>
                        <button className="btn btn-secondary btn-sm" onClick={() => navigate(`/invoices?ticket=${id}`)}><FileText size={14} /> Invoice</button>
                    </div>
                </div>

                {/* Right: Timeline */}
                <div className="card">
                    <h3 className="card-title" style={{ marginBottom: 20 }}>Lifecycle Timeline</h3>
                    <div className="timeline">
                        {statusList.map((st, i) => {
                            const isDone = i < currentIdx;
                            const isActive = i === currentIdx;
                            const isPending = i > currentIdx;
                            return (
                                <div className="timeline-item" key={st}>
                                    <div className={`timeline-dot ${isDone ? 'done' : isActive ? 'active' : 'pending'}`}>
                                        {isDone ? <CheckCircle size={16} /> : isActive ? <Clock size={16} /> : <Circle size={16} />}
                                    </div>
                                    <div className="timeline-content">
                                        <div className="timeline-title">{TICKET_STATUS[st]?.label || st}</div>
                                        {isDone && <div className="timeline-time">Completed</div>}
                                        {isActive && <div className="timeline-time" style={{ color: 'var(--accent-blue)' }}>In Progress</div>}
                                        {isPending && <div className="timeline-time">Pending</div>}
                                    </div>
                                </div>
                            );
                        })}
                    </div>

                    {steps.length > 0 && (
                        <>
                            <h4 style={{ marginTop: 24, marginBottom: 12, fontSize: 14, fontWeight: 600, color: 'var(--text-secondary)' }}>Diagnosis Steps</h4>
                            <div className="timeline">
                                {steps.map((s) => (
                                    <div className="timeline-item" key={s.id}>
                                        <div className={`timeline-dot ${s.result === 'PASS' ? 'done' : 'active'}`}>
                                            {s.result === 'PASS' ? <CheckCircle size={14} /> : <AlertCircle size={14} />}
                                        </div>
                                        <div className="timeline-content">
                                            <div className="timeline-title">{s.stepName || s.name}</div>
                                            <div className="timeline-time">{s.result} • {formatDateTime(s.createdAt)}</div>
                                            {s.notes && <div className="timeline-note">{s.notes}</div>}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </>
                    )}
                </div>
            </div>

            {/* Status Update Modal */}
            <Modal open={showStatusModal} onClose={() => setShowStatusModal(false)} title="Update Ticket Status" footer={
                <><button className="btn btn-secondary" onClick={() => setShowStatusModal(false)}>Cancel</button>
                    <button className="btn btn-primary" onClick={handleStatusUpdate}>Update</button></>
            }>
                <div className="form-group">
                    <label className="form-label">New Status</label>
                    <select className="form-select" value={newStatus} onChange={(e) => setNewStatus(e.target.value)}>
                        <option value="">Select status...</option>
                        {Object.entries(TICKET_STATUS).map(([k, v]) => <option key={k} value={k}>{v.label}</option>)}
                    </select>
                </div>
            </Modal>

            {/* Diagnosis Modal */}
            <Modal open={showDiagModal} onClose={() => setShowDiagModal(false)} title="Add Diagnosis Step" footer={
                <><button className="btn btn-secondary" onClick={() => setShowDiagModal(false)}>Cancel</button>
                    <button className="btn btn-primary" onClick={handleDiagnosis}>Add Step</button></>
            }>
                <div className="form-group">
                    <label className="form-label">Step Name</label>
                    <input className="form-input" placeholder="e.g. GPU Stress Test" value={diagForm.stepName} onChange={(e) => setDiagForm({ ...diagForm, stepName: e.target.value })} />
                </div>
                <div className="form-group">
                    <label className="form-label">Result</label>
                    <select className="form-select" value={diagForm.result} onChange={(e) => setDiagForm({ ...diagForm, result: e.target.value })}>
                        <option value="PASS">PASS</option>
                        <option value="FAIL">FAIL</option>
                        <option value="INCONCLUSIVE">INCONCLUSIVE</option>
                    </select>
                </div>
                <div className="form-group">
                    <label className="form-label">Notes</label>
                    <textarea className="form-textarea" placeholder="Observations..." value={diagForm.notes} onChange={(e) => setDiagForm({ ...diagForm, notes: e.target.value })} />
                </div>
            </Modal>
        </div>
    );
}
