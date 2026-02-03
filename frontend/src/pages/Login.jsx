import { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [localError, setLocalError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const {
        login,
        isAuthenticated,
        tenants,
        tenantSlug,
        selectTenant,
        error,
        clearError
    } = useAuth();

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

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLocalError('');

        if (!tenantSlug) {
            setLocalError('Please select a store');
            return;
        }

        if (!email || !password) {
            setLocalError('Please fill in all fields');
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

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label" htmlFor="tenant">
                            Select Store
                        </label>
                        <select
                            id="tenant"
                            className="form-input form-select"
                            value={tenantSlug || ''}
                            onChange={(e) => selectTenant(e.target.value)}
                        >
                            <option value="">Choose a store...</option>
                            {tenants.map((tenant) => (
                                <option key={tenant.id} value={tenant.tenantSlug}>
                                    {tenant.name}
                                </option>
                            ))}
                        </select>
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
                        />
                    </div>

                    {(localError || error) && (
                        <div className="form-error">
                            ⚠️ {localError || error}
                        </div>
                    )}

                    <button
                        type="submit"
                        className="btn btn-primary btn-lg w-full"
                        disabled={isLoading}
                    >
                        {isLoading ? 'Signing in...' : 'Sign In'}
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
