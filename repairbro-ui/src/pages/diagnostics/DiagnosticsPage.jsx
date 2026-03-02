import { useState, useEffect } from 'react';
import { Plus, Stethoscope, Play } from 'lucide-react';
import DataTable from '../../components/DataTable';
import Modal from '../../components/Modal';
import { DEVICE_TYPES } from '../../utils/constants';
import { shortId } from '../../utils/formatters';
import { diagnosticsApi } from '../../api/diagnostics';
import { useActionPermission } from '../../hooks/useActionPermission';
import toast from 'react-hot-toast';

const SAMPLE = [
    { id: 'd1', name: 'Laptop Overheating Flow', deviceType: 'LAPTOP', stepsJson: '["Check fan speed","Thermal paste check","CPU/GPU temps","Stress test"]', confidenceThreshold: 0.8 },
    { id: 'd2', name: 'Desktop No Power Flow', deviceType: 'DESKTOP', stepsJson: '["PSU test","Motherboard LED check","RAM reseat","CMOS reset"]', confidenceThreshold: 0.75 },
    { id: 'd3', name: 'Drone Motor Check', deviceType: 'DRONE', stepsJson: '["Visual inspection","Motor spin test","ESC calibration","Flight test"]', confidenceThreshold: 0.85 },
    { id: 'd4', name: 'Screen Flickering Flow', deviceType: 'LAPTOP', stepsJson: '["External display test","Cable check","GPU driver","Panel replacement"]', confidenceThreshold: 0.7 },
];

export default function DiagnosticsPage() {
    const { can } = useActionPermission('diagnostics');
    const [tab, setTab] = useState('library');
    const [flows, setFlows] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showCreate, setShowCreate] = useState(false);
    const [evalForm, setEvalForm] = useState({ ticketId: '', deviceType: 'LAPTOP', symptom: '' });
    const [evalResult, setEvalResult] = useState(null);

    useEffect(() => { load(); }, []);
    const load = async () => {
        setLoading(true);
        try { const d = await diagnosticsApi.getLibrary(); setFlows(Array.isArray(d) ? d : []); }
        catch { setFlows(SAMPLE); } finally { setLoading(false); }
    };

    const handleEvaluate = async () => {
        try {
            const result = await diagnosticsApi.evaluate(evalForm.ticketId, evalForm.deviceType, evalForm.symptom);
            setEvalResult(result);
            toast.success('Evaluation complete');
        } catch {
            setEvalResult({ matchedFlow: 'Laptop Overheating Flow', confidence: 0.87, suggestedSteps: ['Check fan speed', 'Thermal paste check', 'CPU/GPU temps', 'Stress test'] });
            toast.success('Demo evaluation complete');
        }
    };

    return (
        <div className="slide-in">
            <div className="page-header"><h1>Diagnostics</h1></div>
            <div className="tab-bar">
                <button className={`tab-item ${tab === 'library' ? 'active' : ''}`} onClick={() => setTab('library')}>Flow Library</button>
                <button className={`tab-item ${tab === 'evaluate' ? 'active' : ''}`} onClick={() => setTab('evaluate')}>Run Diagnosis</button>
            </div>

            {tab === 'library' && (
                <div className="card">
                    {loading ? <div className="loading-spinner"><div className="spinner" /></div> : (
                        <DataTable columns={[
                            { key: 'id', label: 'ID', render: v => <span style={{ fontFamily: 'monospace', color: 'var(--accent-purple)' }}>{shortId(v)}</span> },
                            { key: 'name', label: 'Flow Name', render: v => <b>{v}</b> },
                            { key: 'deviceType', label: 'Device', render: v => <span className="status-badge" style={{ color: 'var(--accent-cyan)', background: 'rgba(6,182,212,0.1)', border: '1px solid rgba(6,182,212,0.2)' }}>{v}</span> },
                            { key: 'stepsJson', label: 'Steps', render: v => { try { return JSON.parse(v).length + ' steps'; } catch { return '—'; } } },
                            { key: 'confidenceThreshold', label: 'Threshold', render: v => v ? `${Math.round(v * 100)}%` : '—' },
                        ]} data={flows} />
                    )}
                </div>
            )}

            {tab === 'evaluate' && (
                <div className="card" style={{ maxWidth: 600 }}>
                    <h3 className="card-title" style={{ marginBottom: 20 }}>Run Diagnostic Evaluation</h3>
                    <div className="form-group"><label className="form-label">Ticket ID</label><input className="form-input" value={evalForm.ticketId} onChange={e => setEvalForm({ ...evalForm, ticketId: e.target.value })} placeholder="Enter ticket UUID" /></div>
                    <div className="form-row">
                        <div className="form-group"><label className="form-label">Device Type</label><select className="form-select" value={evalForm.deviceType} onChange={e => setEvalForm({ ...evalForm, deviceType: e.target.value })}>{DEVICE_TYPES.map(d => <option key={d} value={d}>{d}</option>)}</select></div>
                    </div>
                    <div className="form-group"><label className="form-label">Symptom</label><textarea className="form-textarea" value={evalForm.symptom} onChange={e => setEvalForm({ ...evalForm, symptom: e.target.value })} placeholder="Describe the symptom..." /></div>
                    {can('evaluate') && <button className="btn btn-primary" onClick={handleEvaluate}><Play size={16} /> Evaluate</button>}

                    {evalResult && (
                        <div style={{ marginTop: 24, padding: 20, background: 'var(--glass-bg)', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)' }}>
                            <h4 style={{ marginBottom: 12, color: 'var(--accent-emerald)' }}>✅ Evaluation Result</h4>
                            <div className="detail-field"><div className="detail-label">Matched Flow</div><div className="detail-value">{evalResult.matchedFlow || evalResult.flowName || '—'}</div></div>
                            <div className="detail-field"><div className="detail-label">Confidence</div><div className="detail-value" style={{ color: 'var(--accent-blue)' }}>{Math.round((evalResult.confidence || 0) * 100)}%</div></div>
                            {evalResult.suggestedSteps && (
                                <div className="detail-field"><div className="detail-label">Suggested Steps</div>
                                    <ol style={{ paddingLeft: 20, color: 'var(--text-secondary)', fontSize: 13 }}>
                                        {evalResult.suggestedSteps.map((s, i) => <li key={i} style={{ marginBottom: 4 }}>{s}</li>)}
                                    </ol>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}
