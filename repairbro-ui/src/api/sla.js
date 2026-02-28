import client from './client';

export const slaApi = {
    getByBranch: (branchId) => client.get(`/sla/branch/${branchId}`),
    create: (data) => client.post('/sla', data),
    getComplaints: (branchId) => client.get(`/complaints/branch/${branchId}`),
    fileComplaint: (data) => client.post('/complaints', data),
};
