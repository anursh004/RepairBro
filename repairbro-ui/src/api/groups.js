import client from './client';

export const groupApi = {
    // ── Permission Groups ──
    getAll: () => client.get('/groups').then(r => r.data?.data),
    getById: (id) => client.get(`/groups/${id}`).then(r => r.data?.data),
    create: (data) => client.post('/groups', data).then(r => r.data?.data),
    update: (id, data) => client.put(`/groups/${id}`, data).then(r => r.data?.data),
    remove: (id) => client.delete(`/groups/${id}`).then(r => r.data),
    addPermission: (groupId, permId) => client.post(`/groups/${groupId}/permissions/${permId}`).then(r => r.data?.data),
    removePermission: (groupId, permId) => client.delete(`/groups/${groupId}/permissions/${permId}`).then(r => r.data?.data),

    // ── Permissions Catalog ──
    getPermissions: () => client.get('/permissions').then(r => r.data?.data),
    getModules: () => client.get('/permissions/modules').then(r => r.data?.data),
    getByModule: (mod) => client.get(`/permissions/modules/${mod}`).then(r => r.data?.data),

    // ── User ↔ Group Assignment ──
    getUserGroups: (userId) => client.get(`/users/${userId}/groups`).then(r => r.data?.data),
    assignGroup: (userId, groupId) => client.post(`/users/${userId}/groups`, { groupId }).then(r => r.data),
    removeGroup: (userId, groupId) => client.delete(`/users/${userId}/groups/${groupId}`).then(r => r.data),

    // ── User ↔ Location ──
    getUserLocations: (userId) => client.get(`/users/${userId}/locations`).then(r => r.data?.data),
    assignLocation: (userId, data) => client.post(`/users/${userId}/locations`, data).then(r => r.data),
    removeLocation: (userId, branchId) => client.delete(`/users/${userId}/locations/${branchId}`).then(r => r.data),

    // ── Permission Summary ──
    getPermissionSummary: (userId) => client.get(`/users/${userId}/permissions`).then(r => r.data?.data),
};
