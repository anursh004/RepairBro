import client from './client';

export const branchApi = {
    getAll: () => client.get('/branches'),
    get: (id) => client.get(`/branches/${id}`),
    create: (data) => client.post('/branches', data),
    update: (id, data) => client.put(`/branches/${id}`, data),
    deactivate: (id) => client.delete(`/branches/${id}`),
    getByCity: (city) => client.get(`/branches/city/${city}`),
    getByTier: (tier) => client.get(`/branches/tier/${tier}`),
};
