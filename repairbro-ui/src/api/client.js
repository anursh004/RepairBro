import axios from 'axios';

const client = axios.create({
    baseURL: '/api/v1',
    headers: { 'Content-Type': 'application/json' },
});

// ── Request interceptor: attach JWT ─────────────────────
client.interceptors.request.use((config) => {
    const token = sessionStorage.getItem('rb_token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// ── Response interceptor: unwrap ApiResponse + handle 401
client.interceptors.response.use(
    (res) => {
        // If the backend wraps in ApiResponse, unwrap data
        if (res.data && res.data.data !== undefined) {
            return res.data.data;
        }
        return res.data;
    },
    async (error) => {
        if (error.response?.status === 401) {
            // Try refresh
            const refreshToken = sessionStorage.getItem('rb_refresh');
            if (refreshToken && !error.config._retry) {
                error.config._retry = true;
                try {
                    const res = await axios.post('/api/v1/auth/refresh', { refreshToken });
                    const newToken = res.data?.data?.accessToken || res.data?.accessToken;
                    if (newToken) {
                        sessionStorage.setItem('rb_token', newToken);
                        error.config.headers.Authorization = `Bearer ${newToken}`;
                        return client(error.config);
                    }
                } catch {
                    sessionStorage.clear();
                    window.location.href = '/login';
                }
            } else {
                sessionStorage.clear();
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

export default client;
