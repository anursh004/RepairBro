import { TrendingUp, TrendingDown } from 'lucide-react';

export default function KpiCard({ icon: Icon, label, value, trend, trendLabel, accent = 'blue' }) {
    const colors = {
        blue: { accent: 'var(--accent-blue)', glow: 'var(--accent-blue-glow)' },
        emerald: { accent: 'var(--accent-emerald)', glow: 'var(--accent-emerald-glow)' },
        amber: { accent: 'var(--accent-amber)', glow: 'var(--accent-amber-glow)' },
        purple: { accent: 'var(--accent-purple)', glow: 'var(--accent-purple-glow)' },
        red: { accent: 'var(--accent-red)', glow: 'var(--accent-red-glow)' },
        cyan: { accent: 'var(--accent-cyan)', glow: 'rgba(6, 182, 212, 0.2)' },
    };
    const c = colors[accent] || colors.blue;

    return (
        <div className="kpi-card" style={{ '--kpi-accent': c.accent, '--kpi-glow': c.glow }}>
            <div className="kpi-icon">
                <Icon size={22} />
            </div>
            <div className="kpi-value">{value}</div>
            <div className="kpi-label">{label}</div>
            {(trend !== undefined || trendLabel) && (
                <div className={`kpi-trend ${trend >= 0 ? 'up' : 'down'}`}>
                    {trend >= 0 ? <TrendingUp size={14} /> : <TrendingDown size={14} />}
                    {trendLabel || `${Math.abs(trend)}%`}
                </div>
            )}
        </div>
    );
}
