import client from './client';

export const franchiseApi = {
    getAll: () => client.get('/franchises'),
    get: (id) => client.get(`/franchises/${id}`),
    onboard: (data) => client.post('/franchises', data),
    getRoyalties: (franchiseId) => client.get(`/royalties/${franchiseId}`),
    calculateRoyalty: (franchiseId, period, grossRevenue) =>
        client.post('/royalties/calculate', { franchiseId, period, grossRevenue }),
};
