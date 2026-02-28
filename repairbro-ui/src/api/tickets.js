import client from './client';

export const ticketApi = {
    create: (data) => client.post('/tickets', data),
    get: (id) => client.get(`/tickets/${id}`),
    getByBranch: (branchId, status, page = 0, size = 20) => {
        const params = { page, size };
        if (status) params.status = status;
        return client.get(`/tickets/branch/${branchId}`, { params });
    },
    updateStatus: (id, data) => client.post(`/tickets/${id}/status`, data),
    addDiagnosis: (id, data) => client.post(`/tickets/${id}/diagnose`, data),
    assignTech: (id, techId) => client.post(`/tickets/${id}/assign/${techId}`),
};
