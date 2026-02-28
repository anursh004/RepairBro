import client from './client';

export const technicianApi = {
    getAll: () => client.get('/technicians'),
    create: (data) => client.post('/technicians', data),
    getByUser: (userId) => client.get(`/technicians/user/${userId}`),
    getByBranch: (branchId) => client.get(`/technicians/branch/${branchId}`),
    update: (profileId, data) => client.put(`/technicians/${profileId}`, data),
};
