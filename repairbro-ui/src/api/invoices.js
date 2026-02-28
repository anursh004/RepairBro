import client from './client';

export const invoiceApi = {
    create: (data) => client.post('/invoices', data),
    get: (id) => client.get(`/invoices/${id}`),
    getByTicket: (ticketId) => client.get(`/invoices/ticket/${ticketId}`),
    getByBranch: (branchId, page = 0, size = 20) =>
        client.get(`/invoices/branch/${branchId}`, { params: { page, size } }),
    recordPayment: (id, data) => client.post(`/invoices/${id}/payments`, data),
};

export const estimateApi = {
    create: (data) => client.post('/estimates', data),
    get: (id) => client.get(`/estimates/${id}`),
    getByTicket: (ticketId) => client.get(`/estimates/ticket/${ticketId}`),
    getByBranch: (branchId) => client.get(`/estimates/branch/${branchId}`),
    approve: (id) => client.post(`/estimates/${id}/approve`),
    reject: (id) => client.post(`/estimates/${id}/reject`),
};
