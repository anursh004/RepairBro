import client from './client';

export const authApi = {
    login: (email, password) => client.post('/auth/login', { email, password }),
    register: (data) => client.post('/auth/register', data),
    refresh: (refreshToken) => client.post('/auth/refresh', { refreshToken }),
    getAllUsers: () => client.get('/users'),
    getUser: (userId) => client.get(`/users/${userId}`),
};
