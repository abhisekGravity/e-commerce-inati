import { useState, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import { cartApi } from './api/api';

// Components
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';

// Pages
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Products from './pages/Products';
import Cart from './pages/Cart';
import Admin from './pages/Admin';

function App() {
    const { isAuthenticated } = useAuth();
    const [cartItemCount, setCartItemCount] = useState(0);

    // Fetch cart item count when authenticated
    useEffect(() => {
        const fetchCartCount = async () => {
            if (!isAuthenticated) {
                setCartItemCount(0);
                return;
            }

            try {
                const cart = await cartApi.get();
                const count = cart?.items?.reduce((sum, item) => sum + item.quantity, 0) || 0;
                setCartItemCount(count);
            } catch (err) {
                console.error('Failed to fetch cart count:', err);
                setCartItemCount(0);
            }
        };

        fetchCartCount();

        // Refresh cart count periodically
        const interval = setInterval(fetchCartCount, 30000);
        return () => clearInterval(interval);
    }, [isAuthenticated]);

    return (
        <div className="app">
            <Navbar cartItemCount={cartItemCount} />

            <Routes>
                {/* Public Routes */}
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/products" element={<Products />} />

                {/* Protected Routes */}
                <Route
                    path="/cart"
                    element={
                        <ProtectedRoute>
                            <Cart />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/admin"
                    element={
                        <ProtectedRoute>
                            <Admin />
                        </ProtectedRoute>
                    }
                />

                {/* Fallback Route */}
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </div>
    );
}

export default App;
