import client from './client';

export const customerApi = {
    getAll: () => client.get('/customers'),
    get: (id) => client.get(`/customers/${id}`),
    create: (data) => client.post('/customers', data),
    update: (id, data) => client.put(`/customers/${id}`, data),
    getByPhone: (phone) => client.get(`/customers/phone/${phone}`),
    search: (name) => client.get('/customers/search', { params: { name } }),
};
