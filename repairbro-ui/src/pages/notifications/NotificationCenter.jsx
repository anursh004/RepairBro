import { useState, useEffect } from 'react';
import { Bell, Send, AlertCircle, CheckCircle, Mail } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import { formatDateTime } from '../../utils/formatters';
import { notificationApi } from '../../api/notifications';
import toast from 'react-hot-toast';

const SAMPLE = Array.from({ length: 15 }, (_, i) => ({
    id: `n-${i}`, channel: i % 3 === 0 ? 'SMS' : 'EMAIL', recipientEmail: `user${i}@mail.com`,
    subject: ['Ticket Update', 'Invoice Ready', 'SLA Alert', 'Repair Done', 'Welcome'][i % 5],
    eventType: ['TICKET_CREATED', 'INVOICE_SENT', 'SLA_BREACH', 'COMPLETED', 'REGISTER'][i % 5],
    status: i % 6 === 0 ? 'FAILED' : 'SENT', createdAt: new Date(Date.now() - i * 3600000).toISOString(),
}));

export default function NotificationCenter() {
    const [tab, setTab] = useState('recent');
    const [notifs, setNotifs] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showSend, setShowSend] = useState(false);
    const [sendForm, setSendForm] = useState({ channel: 'EMAIL', recipient: '', subject: '', body: '' });

    useEffect(() => { load(); }, []);
    const load = async () => {
        setLoading(true);
        try { const d = await notificationApi.getRecent(); setNotifs(Array.isArray(d) ? d : []); }
        catch { setNotifs(SAMPLE); } finally { setLoading(false); }
    };

    const handleSend = async () => {
        try { await notificationApi.sendManual(sendForm); toast.success('Sent!'); setShowSend(false); }
        catch { toast.error('Failed'); }
    };

    const data = tab === 'failed' ? notifs.filter(n => n.status === 'FAILED') : notifs;

    return (
        <div className="slide-in">
            <div className="page-header">
                <h1>Notifications</h1>
                <button className="btn btn-primary" onClick={() => setShowSend(true)}><Send size={16} /> Send</button>
            </div>
            <div className="tab-bar">
                <button className={`tab-item ${tab === 'recent' ? 'active' : ''}`} onClick={() => setTab('recent')}>Recent</button>
                <button className={`tab-item ${tab === 'failed' ? 'active' : ''}`} onClick={() => setTab('failed')}>Failed</button>
            </div>
            <div className="card">
                {loading ? <div className="loading-spinner"><div className="spinner" /></div> : (
                    <DataTable columns={[
                        { key: 'channel', label: 'Channel', render: v => <span className="status-badge" style={{ color: v === 'EMAIL' ? 'var(--accent-blue)' : 'var(--accent-purple)', background: v === 'EMAIL' ? 'var(--accent-blue-glow)' : 'var(--accent-purple-glow)' }}>{v}</span> },
                        { key: 'recipientEmail', label: 'To' },
                        { key: 'subject', label: 'Subject', render: v => <b>{v}</b> },
                        { key: 'eventType', label: 'Event' },
                        { key: 'status', label: 'Status', render: v => v === 'SENT' ? <span style={{ color: 'var(--accent-emerald)' }}>✓ Sent</span> : <span style={{ color: 'var(--accent-red)' }}>✗ Failed</span> },
                        { key: 'createdAt', label: 'Time', render: v => formatDateTime(v) },
                    ]} data={data} />
                )}
            </div>
            <Modal open={showSend} onClose={() => setShowSend(false)} title="Send Notification" footer={<><button className="btn btn-secondary" onClick={() => setShowSend(false)}>Cancel</button><button className="btn btn-primary" onClick={handleSend}>Send</button></>}>
                <div className="form-group"><label className="form-label">Channel</label><select className="form-select" value={sendForm.channel} onChange={e => setSendForm({ ...sendForm, channel: e.target.value })}><option value="EMAIL">Email</option><option value="SMS">SMS</option></select></div>
                <div className="form-group"><label className="form-label">To</label><input className="form-input" value={sendForm.recipient} onChange={e => setSendForm({ ...sendForm, recipient: e.target.value })} /></div>
                <div className="form-group"><label className="form-label">Subject</label><input className="form-input" value={sendForm.subject} onChange={e => setSendForm({ ...sendForm, subject: e.target.value })} /></div>
                <div className="form-group"><label className="form-label">Body</label><textarea className="form-textarea" value={sendForm.body} onChange={e => setSendForm({ ...sendForm, body: e.target.value })} /></div>
            </Modal>
        </div>
    );
}
