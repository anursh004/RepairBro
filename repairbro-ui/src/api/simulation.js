import client from './client';

export const simulationApi = {
    createScenario: (data) => client.post('/simulation/scenarios', data),
    listScenarios: () => client.get('/simulation/scenarios'),
    getScenario: (id) => client.get(`/simulation/scenarios/${id}`),
    run: (id) => client.post(`/simulation/scenarios/${id}/run`),
    getResults: (id) => client.get(`/simulation/scenarios/${id}/results`),
};
