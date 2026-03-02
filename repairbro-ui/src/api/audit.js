import client from './client';

export const auditApi = {
    getActions: (params) => client.get('/auth/api/audit/actions', { params }).then(r => r.data),
    getLoginHistory: (params) => client.get('/auth/api/audit/logins', { params }).then(r => r.data),
    getPermissionChanges: (params) => client.get('/auth/api/audit/permissions', { params }).then(r => r.data),
};
