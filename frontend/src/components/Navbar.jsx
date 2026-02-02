import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar({ cartItemCount = 0 }) {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const { isAuthenticated, tenantSlug, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = async () => {
        await logout();
        navigate('/login');
    };

    const toggleMenu = () => {
        setIsMenuOpen(!isMenuOpen);
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                <Link to="/" className="navbar-brand">
                    🛒 ShopPro
                </Link>

                <button className="navbar-toggle" onClick={toggleMenu} aria-label="Toggle menu">
                    <span></span>
                    <span></span>
                    <span></span>
                </button>

                <ul className={`navbar-nav ${isMenuOpen ? 'open' : ''}`}>
                    <li>
                        <NavLink
                            to="/products"
                            className={({ isActive }) => `navbar-link ${isActive ? 'active' : ''}`}
                            onClick={() => setIsMenuOpen(false)}
                        >
                            Products
                        </NavLink>
                    </li>
                    {isAuthenticated && (
                        <>
                            <li>
                                <NavLink
                                    to="/cart"
                                    className={({ isActive }) => `navbar-link ${isActive ? 'active' : ''}`}
                                    onClick={() => setIsMenuOpen(false)}
                                >
                                    Cart
                                </NavLink>
                            </li>
                            <li>
                                <NavLink
                                    to="/admin"
                                    className={({ isActive }) => `navbar-link ${isActive ? 'active' : ''}`}
                                    onClick={() => setIsMenuOpen(false)}
                                >
                                    Admin
                                </NavLink>
                            </li>
                        </>
                    )}
                </ul>

                <div className="navbar-actions">
                    {tenantSlug && (
                        <span className="tenant-badge">{tenantSlug}</span>
                    )}

                    {isAuthenticated ? (
                        <>
                            <Link to="/cart" className="btn btn-ghost cart-badge">
                                🛒
                                {cartItemCount > 0 && (
                                    <span className="cart-badge-count">{cartItemCount}</span>
                                )}
                            </Link>
                            <button className="btn btn-secondary" onClick={handleLogout}>
                                Logout
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="btn btn-ghost">
                                Sign In
                            </Link>
                            <Link to="/register" className="btn btn-primary">
                                Sign Up
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
