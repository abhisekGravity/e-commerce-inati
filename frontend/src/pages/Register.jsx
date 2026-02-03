import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Register() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [localError, setLocalError] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const {
        register,
        isAuthenticated,
        tenants,
        tenantSlug,
        selectTenant,
        error,
        clearError
    } = useAuth();

    const navigate = useNavigate();

    // Redirect if already authenticated
    useEffect(() => {
        if (isAuthenticated) {
            navigate('/products', { replace: true });
        }
    }, [isAuthenticated, navigate]);

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

        if (!email || !password || !confirmPassword) {
            setLocalError('Please fill in all fields');
            return;
        }

        if (password.length < 4) {
            setLocalError('Password must be at least 4 characters');
            return;
        }

        if (password !== confirmPassword) {
            setLocalError('Passwords do not match');
            return;
        }

        setIsLoading(true);
        const result = await register(email, password);
        setIsLoading(false);

        if (result.success) {
            navigate('/products', { replace: true });
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <div className="auth-header">
                    <h1 className="text-gradient">Create Account</h1>
                    <p>Join us and start shopping today</p>
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
                        {tenants.length === 0 && (
                            <div className="form-error" style={{ marginTop: '0.5rem' }}>
                                ⚠️ No stores available. Please contact admin.
                            </div>
                        )}
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
                            placeholder="At least 4 characters"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            autoComplete="new-password"
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label" htmlFor="confirmPassword">
                            Confirm Password
                        </label>
                        <input
                            type="password"
                            id="confirmPassword"
                            className="form-input"
                            placeholder="••••••••"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            autoComplete="new-password"
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
                        {isLoading ? 'Creating account...' : 'Create Account'}
                    </button>
                </form>

                <div className="auth-footer">
                    Already have an account? <Link to="/login">Sign in</Link>
                </div>
            </div>
        </div>
    );
}

export default Register;