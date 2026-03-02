export function exportToCsv(filename, columns, data) {
    const headers = columns.map(c => c.label).join(',');
    const rows = data.map(row =>
        columns.map(c => {
            const val = row[c.key];
            const str = val == null ? '' : String(val);
            return str.includes(',') || str.includes('"') ? `"${str.replace(/"/g, '""')}"` : str;
        }).join(',')
    );
    const csv = [headers, ...rows].join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
}
