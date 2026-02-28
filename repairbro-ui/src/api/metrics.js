import client from './client';

export const metricsApi = {
    getBranchMetrics: (branchId, from, to) =>
        client.get(`/metrics/branch/${branchId}`, { params: { from, to } }),
    getDailyReport: (date) =>
        client.get('/reports/daily', { params: { date } }),
};
