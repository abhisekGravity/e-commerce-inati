import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Home() {
    const { isAuthenticated } = useAuth();

    return (
        <div className="page">
            <div className="container">
                {/* Hero Section */}
                <section style={{
                    textAlign: 'center',
                    padding: 'var(--spacing-3xl) 0',
                    maxWidth: '800px',
                    margin: '0 auto',
                }}>
                    <h1 style={{
                        fontSize: 'clamp(2.5rem, 5vw, 4rem)',
                        fontWeight: 800,
                        lineHeight: 1.1,
                        marginBottom: 'var(--spacing-lg)',
                    }}>
                        Welcome to{' '}
                        <span className="text-gradient">ShopPro</span>
                    </h1>

                    <p style={{
                        fontSize: 'var(--font-size-xl)',
                        color: 'var(--color-text-secondary)',
                        marginBottom: 'var(--spacing-2xl)',
                        lineHeight: 1.6,
                    }}>
                        A premium multi-tenant e-commerce platform with a beautiful shopping experience.
                        Discover amazing products and enjoy seamless checkout.
                    </p>

                    <div style={{ display: 'flex', gap: 'var(--spacing-md)', justifyContent: 'center', flexWrap: 'wrap' }}>
                        <Link to="/products" className="btn btn-primary btn-lg">
                            Browse Products
                        </Link>
                        {!isAuthenticated && (
                            <Link to="/register" className="btn btn-secondary btn-lg">
                                Create Account
                            </Link>
                        )}
                    </div>
                </section>

                {/* Features Grid */}
                <section style={{ padding: 'var(--spacing-2xl) 0' }}>
                    <h2 style={{ textAlign: 'center', marginBottom: 'var(--spacing-2xl)' }}>
                        Why Choose <span className="text-gradient">ShopPro</span>?
                    </h2>

                    <div className="grid grid-3">
                        <div className="card" style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '3rem', marginBottom: 'var(--spacing-md)' }}>🛒</div>
                            <h3 style={{ fontSize: 'var(--font-size-xl)', marginBottom: 'var(--spacing-sm)' }}>
                                Easy Shopping
                            </h3>
                            <p className="text-secondary">
                                Browse products, add to cart, and checkout with just a few clicks.
                            </p>
                        </div>

                        <div className="card" style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '3rem', marginBottom: 'var(--spacing-md)' }}>🔒</div>
                            <h3 style={{ fontSize: 'var(--font-size-xl)', marginBottom: 'var(--spacing-sm)' }}>
                                Secure Auth
                            </h3>
                            <p className="text-secondary">
                                JWT-based authentication with secure token refresh and logout.
                            </p>
                        </div>

                        <div className="card" style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '3rem', marginBottom: 'var(--spacing-md)' }}>🏪</div>
                            <h3 style={{ fontSize: 'var(--font-size-xl)', marginBottom: 'var(--spacing-sm)' }}>
                                Multi-Tenant
                            </h3>
                            <p className="text-secondary">
                                Each store operates independently with isolated data and products.
                            </p>
                        </div>

                        <div className="card" style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '3rem', marginBottom: 'var(--spacing-md)' }}>📦</div>
                            <h3 style={{ fontSize: 'var(--font-size-xl)', marginBottom: 'var(--spacing-sm)' }}>
                                Inventory Tracking
                            </h3>
                            <p className="text-secondary">
                                Real-time inventory management with stock validation on orders.
                            </p>
                        </div>

                        <div className="card" style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '3rem', marginBottom: 'var(--spacing-md)' }}>🔍</div>
                            <h3 style={{ fontSize: 'var(--font-size-xl)', marginBottom: 'var(--spacing-sm)' }}>
                                Smart Filters
                            </h3>
                            <p className="text-secondary">
                                Filter by price, availability, and sort by multiple criteria.
                            </p>
                        </div>

                        <div className="card" style={{ textAlign: 'center' }}>
                            <div style={{ fontSize: '3rem', marginBottom: 'var(--spacing-md)' }}>⚡</div>
                            <h3 style={{ fontSize: 'var(--font-size-xl)', marginBottom: 'var(--spacing-sm)' }}>
                                Fast & Modern
                            </h3>
                            <p className="text-secondary">
                                Built with React and modern technologies for speed and reliability.
                            </p>
                        </div>
                    </div>
                </section>

                {/* CTA Section */}
                <section style={{
                    textAlign: 'center',
                    padding: 'var(--spacing-3xl)',
                    background: 'var(--color-bg-card)',
                    borderRadius: 'var(--border-radius-xl)',
                    border: '1px solid var(--border-color)',
                    marginTop: 'var(--spacing-2xl)',
                }}>
                    <h2 style={{ marginBottom: 'var(--spacing-md)' }}>
                        Ready to start shopping?
                    </h2>
                    <p className="text-secondary" style={{ marginBottom: 'var(--spacing-lg)' }}>
                        Sign up now and explore our collection of premium products.
                    </p>
                    <Link to="/products" className="btn btn-primary btn-lg">
                        Get Started →
                    </Link>
                </section>
            </div>
        </div>
    );
}

export default Home;
