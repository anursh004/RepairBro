import client from './client';

export const inventoryApi = {
    getParts: () => client.get('/parts'),
    createPart: (data) => client.post('/parts', data),
    getInventory: (branchId) => client.get(`/inventory/${branchId}`),
    getLowStock: (branchId) => client.get(`/inventory/${branchId}/low-stock`),
    reserve: (branchId, sparePartId, quantity) =>
        client.post('/inventory/reserve', { branchId, sparePartId, quantity: String(quantity) }),
    placeOrder: (data) => client.post('/procurement/order', data),
};
