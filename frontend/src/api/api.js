import axios from 'axios';
import { getErrorMessage } from './errorUtils';

// Base API configuration
const API_BASE_URL = '/api';

// Create axios instance
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor - add auth token and tenant slug
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        const tenantSlug = localStorage.getItem('tenantSlug');

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        if (tenantSlug) {
            config.headers['x-tenant-slug'] = tenantSlug;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

// Response interceptor - handle 401 and token refresh
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // If 401 and not already retrying, try to refresh token
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem('refreshToken');
                if (!refreshToken) throw new Error('No refresh token');

                const response = await axios.post(
                    `${API_BASE_URL}/auth/refresh`,
                    refreshToken,
                    { headers: { 'Content-Type': 'text/plain' } }
                );

                const { accessToken, refreshToken: newRefreshToken } = response.data;
                localStorage.setItem('accessToken', accessToken);
                if (newRefreshToken) {
                    localStorage.setItem('refreshToken', newRefreshToken);
                }

                // Retry original request with new token
                originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                return api(originalRequest);
            } catch (refreshError) {
                // Refresh failed - clear auth and redirect to login
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                localStorage.removeItem('tenantSlug');
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

// ==========================================
// Auth API
// ==========================================
export const authApi = {
    register: async (email, password) => {
        const response = await api.post('/auth/register', { email, password });
        return response.data;
    },

    login: async (email, password) => {
        const response = await api.post('/auth/login', { email, password });
        return response.data;
    },

    logout: async () => {
        const response = await api.post('/auth/logout');
        return response.data;
    },
};

// ==========================================
// Tenant API
// ==========================================
export const tenantApi = {
    getAll: async () => {
        const response = await api.get('/tenants/getAll');
        return response.data;
    },

    create: async (name) => {
        const response = await api.post('/tenants/create', { name });
        return response.data;
    },
};

// ==========================================
// Product API
// ==========================================
export const productApi = {
    list: async (params = {}) => {
        const queryParams = new URLSearchParams();

        if (params.sku) queryParams.append('sku', params.sku);
        if (params.name) queryParams.append('name', params.name);
        if (params.minPrice !== undefined) queryParams.append('minPrice', params.minPrice);
        if (params.maxPrice !== undefined) queryParams.append('maxPrice', params.maxPrice);
        if (params.inStock !== undefined) queryParams.append('inStock', params.inStock);
        if (params.sortBy) queryParams.append('sortBy', params.sortBy);
        if (params.direction) queryParams.append('direction', params.direction);
        if (params.limit) queryParams.append('limit', params.limit);
        if (params.offset !== undefined) queryParams.append('offset', params.offset);

        const response = await api.get(`/products?${queryParams.toString()}`);
        return response.data;
    },

    create: async (productData) => {
        const response = await api.post('/products', productData);
        return response.data;
    },
};

// ==========================================
// Cart API
// ==========================================
export const cartApi = {
    get: async () => {
        const response = await api.get('/cart');
        return response.data;
    },

    addItem: async (sku, quantity) => {
        const response = await api.post('/cart/add', null, {
            params: { sku, quantity },
        });
        return response.data;
    },
};

// ==========================================
// Order API
// ==========================================
export const orderApi = {
    place: async (idempotencyKey) => {
        const response = await api.post('/orders', null, {
            headers: {
                'Idempotency-Key': idempotencyKey,
            },
        });
        return response.data;
    },
};

export { getErrorMessage };
export default api;
