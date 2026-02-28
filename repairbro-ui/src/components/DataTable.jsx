import { useState } from 'react';
import { ChevronUp, ChevronDown, ChevronLeft, ChevronRight } from 'lucide-react';

export default function DataTable({ columns, data, onRowClick, pageSize = 15 }) {
    const [sortCol, setSortCol] = useState(null);
    const [sortDir, setSortDir] = useState('asc');
    const [page, setPage] = useState(0);

    const handleSort = (key) => {
        if (sortCol === key) {
            setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
        } else {
            setSortCol(key);
            setSortDir('asc');
        }
    };

    let sorted = [...(data || [])];
    if (sortCol) {
        sorted.sort((a, b) => {
            const va = a[sortCol], vb = b[sortCol];
            if (va == null) return 1;
            if (vb == null) return -1;
            if (typeof va === 'number') return sortDir === 'asc' ? va - vb : vb - va;
            return sortDir === 'asc'
                ? String(va).localeCompare(String(vb))
                : String(vb).localeCompare(String(va));
        });
    }

    const totalPages = Math.ceil(sorted.length / pageSize);
    const paged = sorted.slice(page * pageSize, (page + 1) * pageSize);

    return (
        <div>
            <div className="data-table-wrapper">
                <table className="data-table">
                    <thead>
                        <tr>
                            {columns.map((col) => (
                                <th key={col.key} onClick={() => col.sortable !== false && handleSort(col.key)}>
                                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
                                        {col.label}
                                        {sortCol === col.key && (
                                            sortDir === 'asc' ? <ChevronUp size={12} /> : <ChevronDown size={12} />
                                        )}
                                    </span>
                                </th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {paged.length === 0 ? (
                            <tr><td colSpan={columns.length} style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>No data found</td></tr>
                        ) : (
                            paged.map((row, i) => (
                                <tr
                                    key={row.id || i}
                                    className={onRowClick ? 'clickable' : ''}
                                    onClick={() => onRowClick?.(row)}
                                >
                                    {columns.map((col) => (
                                        <td key={col.key}>
                                            {col.render ? col.render(row[col.key], row) : (row[col.key] ?? '—')}
                                        </td>
                                    ))}
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {totalPages > 1 && (
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '14px 16px', borderTop: '1px solid var(--border-default)' }}>
                    <span className="text-sm text-muted">
                        Showing {page * pageSize + 1}–{Math.min((page + 1) * pageSize, sorted.length)} of {sorted.length}
                    </span>
                    <div style={{ display: 'flex', gap: 8 }}>
                        <button className="btn btn-ghost btn-sm" disabled={page === 0} onClick={() => setPage(page - 1)}>
                            <ChevronLeft size={14} /> Prev
                        </button>
                        <button className="btn btn-ghost btn-sm" disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
                            Next <ChevronRight size={14} />
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
