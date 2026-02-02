import { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { tenantApi } from '../api/api';

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [localError, setLocalError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    // Tenant creation state
    const [showCreateTenant, setShowCreateTenant] = useState(false);
    const [newTenantName, setNewTenantName] = useState('');
    const [tenantCreating, setTenantCreating] = useState(false);
    const [tenantSuccess, setTenantSuccess] = useState('');

    const { login, isAuthenticated, tenants, tenantSlug, selectTenant, fetchTenants, error, clearError } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const from = location.state?.from?.pathname || '/products';

    // Redirect if already authenticated
    useEffect(() => {
        if (isAuthenticated) {
            navigate(from, { replace: true });
        }
    }, [isAuthenticated, navigate, from]);

    // Clear errors on mount
    useEffect(() => {
        clearError();
    }, [clearError]);

    // Show create tenant form if no tenants exist
    useEffect(() => {
        if (tenants.length === 0) {
            setShowCreateTenant(true);
        }
    }, [tenants]);

    const handleCreateTenant = async (e) => {
        e.preventDefault();
        setLocalError('');
        setTenantSuccess('');

        if (!newTenantName || newTenantName.length < 3 || newTenantName.length > 10) {
            setLocalError('Store name must be 3-10 characters');
            return;
        }

        setTenantCreating(true);

        try {
            const tenant = await tenantApi.create(newTenantName);
            setTenantSuccess(`Store "${tenant.name}" created! You can now sign in.`);
            setNewTenantName('');
            setShowCreateTenant(false);
            await fetchTenants();
            selectTenant(tenant.tenantSlug);
        } catch (err) {
            console.error('Failed to create tenant:', err);
            const message = err.response?.data?.message || 'Failed to create store. Please try again.';
            setLocalError(message);
        } finally {
            setTenantCreating(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLocalError('');

        // Validation
        if (!email || !password) {
            setLocalError('Please fill in all fields');
            return;
        }

        if (!tenantSlug) {
            setLocalError('Please select a store');
            return;
        }

        setIsLoading(true);

        const result = await login(email, password);

        setIsLoading(false);

        if (result.success) {
            navigate(from, { replace: true });
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <div className="auth-header">
                    <h1 className="text-gradient">Welcome Back</h1>
                    <p>Sign in to your account to continue</p>
                </div>

                {/* Create Tenant Section - Shows when no tenants exist or user clicks button */}
                {showCreateTenant && (
                    <div className="tenant-create-section" style={{
                        background: 'rgba(138, 43, 226, 0.1)',
                        border: '1px solid rgba(138, 43, 226, 0.3)',
                        borderRadius: '12px',
                        padding: '1.5rem',
                        marginBottom: '1.5rem'
                    }}>
                        <h3 style={{ margin: '0 0 0.5rem 0', color: 'var(--primary)' }}>
                            🏪 {tenants.length === 0 ? 'No stores found! Create one first' : 'Create New Store'}
                        </h3>
                        <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', margin: '0 0 1rem 0' }}>
                            Create a store to get started with the platform.
                        </p>
                        <form onSubmit={handleCreateTenant}>
                            <div className="form-group" style={{ marginBottom: '1rem' }}>
                                <input
                                    type="text"
                                    className="form-input"
                                    placeholder="Store name (3-10 chars)"
                                    value={newTenantName}
                                    onChange={(e) => setNewTenantName(e.target.value)}
                                    minLength={3}
                                    maxLength={10}
                                />
                            </div>
                            <div style={{ display: 'flex', gap: '0.5rem' }}>
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={tenantCreating}
                                    style={{ flex: 1 }}
                                >
                                    {tenantCreating ? 'Creating...' : 'Create Store'}
                                </button>
                                {tenants.length > 0 && (
                                    <button
                                        type="button"
                                        className="btn btn-secondary"
                                        onClick={() => setShowCreateTenant(false)}
                                    >
                                        Cancel
                                    </button>
                                )}
                            </div>
                        </form>
                    </div>
                )}

                {tenantSuccess && (
                    <div className="form-success" style={{
                        background: 'rgba(34, 197, 94, 0.1)',
                        border: '1px solid rgba(34, 197, 94, 0.3)',
                        borderRadius: '8px',
                        padding: '0.75rem 1rem',
                        marginBottom: '1rem',
                        color: '#22c55e'
                    }}>
                        ✅ {tenantSuccess}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label" htmlFor="tenant">
                            Select Store
                        </label>
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                            <select
                                id="tenant"
                                className="form-input form-select"
                                value={tenantSlug || ''}
                                onChange={(e) => selectTenant(e.target.value)}
                                style={{ flex: 1 }}
                            >
                                <option value="">Choose a store...</option>
                                {tenants.map((tenant) => (
                                    <option key={tenant.id} value={tenant.tenantSlug}>
                                        {tenant.name}
                                    </option>
                                ))}
                            </select>
                            {!showCreateTenant && (
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() => setShowCreateTenant(true)}
                                    title="Create new store"
                                    style={{ padding: '0 1rem' }}
                                >
                                    +
                                </button>
                            )}
                        </div>
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="email">
                            Email Address
                        </label>
                        <input
                            type="email"
                            id="email"
                            className="form-input"
                            placeholder="you@example.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            autoComplete="email"
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="password">
                            Password
                        </label>
                        <input
                            type="password"
                            id="password"
                            className="form-input"
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            autoComplete="current-password"
                        />
                    </div>

                    {(localError || error) && (
                        <div className="form-error" style={{ marginBottom: '1rem' }}>
                            ⚠️ {localError || error}
                        </div>
                    )}

                    <button
                        type="submit"
                        className="btn btn-primary btn-lg w-full"
                        disabled={isLoading}
                    >
                        {isLoading ? (
                            <>
                                <span className="loading-spinner" style={{ width: 20, height: 20 }}></span>
                                Signing in...
                            </>
                        ) : (
                            'Sign In'
                        )}
                    </button>
                </form>

                <div className="auth-footer">
                    Don't have an account?{' '}
                    <Link to="/register">Create one</Link>
                </div>
            </div>
        </div>
    );
}

export default Login;
