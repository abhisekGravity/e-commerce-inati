import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { authApi, tenantApi, getErrorMessage } from '../api/api';
import Cookies from 'js-cookie';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [accessToken, setAccessToken] = useState(localStorage.getItem('accessToken'));
    const [refreshToken, setRefreshToken] = useState(localStorage.getItem('refreshToken'));
    const [tenantSlug, setTenantSlug] = useState(localStorage.getItem('tenantSlug'));
    const [tenants, setTenants] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Check if user is authenticated
    const isAuthenticated = !!accessToken;

    // Fetch available tenants
    const fetchTenants = useCallback(async () => {
        try {
            const data = await tenantApi.getAll();
            setTenants(data || []);
        } catch (err) {
            console.error('Failed to fetch tenants:', err);
            setTenants([]);
        }
    }, []);

    // Initialize on mount
    useEffect(() => {
        fetchTenants();
        setLoading(false);
    }, [fetchTenants]);

    // Select tenant
    const selectTenant = useCallback((slug) => {
        setTenantSlug(slug);
        localStorage.setItem('tenantSlug', slug);
    }, []);

    // Login
    const login = useCallback(async (email, password) => {
        try {
            setError(null);
            const response = await authApi.login(email, password);
            const token = response.accessToken;
            const refresh = response.refreshToken;

            setAccessToken(token);
            setRefreshToken(refresh);
            localStorage.setItem('accessToken', token);
            localStorage.setItem('refreshToken', refresh);

            // Decode JWT to get user info (basic decode)
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                setUser({ id: payload.sub, tenantId: payload.tenantId });
            } catch {
                setUser({ authenticated: true });
            }

            return { success: true };
        } catch (err) {
            const message = getErrorMessage(err, 'Login failed. Please check your credentials.');
            setError(message);
            return { success: false, error: message };
        }
    }, []);

    // Register
    const register = useCallback(async (email, password) => {
        try {
            setError(null);
            const response = await authApi.register(email, password);
            const token = response.accessToken;
            const refresh = response.refreshToken;

            setAccessToken(token);
            setRefreshToken(refresh);
            localStorage.setItem('accessToken', token);
            localStorage.setItem('refreshToken', refresh);

            // Decode JWT to get user info
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                setUser({ id: payload.sub, tenantId: payload.tenantId });
            } catch {
                setUser({ authenticated: true });
            }

            return { success: true };
        } catch (err) {
            const message = getErrorMessage(err, 'Registration failed. Please try again.');
            setError(message);
            return { success: false, error: message };
        }
    }, []);

    // Logout
    const logout = useCallback(async () => {
        try {
            await authApi.logout();
        } catch (err) {
            console.error('Logout error:', err);
        } finally {
            setAccessToken(null);
            setRefreshToken(null);
            setUser(null);
            setTenantSlug(null);
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('tenantSlug');

            // Extra safety: clear any non-HttpOnly cookies with js-cookie
            Cookies.remove('accessToken');
            Cookies.remove('refresh_token', { path: '/' });
        }
    }, []);

    // Clear error
    const clearError = useCallback(() => {
        setError(null);
    }, []);

    const value = {
        user,
        accessToken,
        tenantSlug,
        tenants,
        loading,
        error,
        isAuthenticated,
        login,
        register,
        logout,
        selectTenant,
        fetchTenants,
        clearError,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}

export default AuthContext;
