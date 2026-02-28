import client from './client';

export const notificationApi = {
    getRecent: () => client.get('/notifications'),
    getByTicket: (ticketId) => client.get(`/notifications/ticket/${ticketId}`),
    getByCustomer: (customerId) => client.get(`/notifications/customer/${customerId}`),
    getFailed: () => client.get('/notifications/failed'),
    testEmail: (to) => client.post('/notifications/test-email', { to }),
    sendManual: (data) => client.post('/notifications/send', data),
};
