import client from './client';

export const diagnosticsApi = {
    getLibrary: () => client.get('/diagnostics/library'),
    getFlow: (id) => client.get(`/diagnostics/${id}`),
    getByDevice: (deviceType) => client.get(`/diagnostics/device/${deviceType}`),
    createFlow: (data) => client.post('/diagnostics', data),
    evaluate: (ticketId, deviceType, symptom) =>
        client.post('/diagnostics/evaluate', { ticketId, deviceType, symptom }),
};
