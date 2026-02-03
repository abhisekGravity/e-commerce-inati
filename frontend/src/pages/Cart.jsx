import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { cartApi, orderApi, getErrorMessage } from '../api/api';
import CartItem from '../components/CartItem';
import { v4 as uuidv4 } from 'uuid';

function Cart() {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [orderLoading, setOrderLoading] = useState(false);
    const [orderSuccess, setOrderSuccess] = useState(null);
    const [toast, setToast] = useState(null);

    // Fetch cart
    const fetchCart = useCallback(async () => {
        setLoading(true);
        setError(null);

        try {
            const data = await cartApi.get();
            setCart(data);
        } catch (err) {
            console.error('Failed to fetch cart:', err);
            setError(getErrorMessage(err, 'Failed to load cart. Please try again.'));
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchCart();
    }, [fetchCart]);

    // Show toast
    const showToast = (type, message) => {
        setToast({ type, message });
        setTimeout(() => setToast(null), 4000);
    };

    // Update quantity
    const handleUpdateQuantity = async (sku, newQuantity) => {
        if (newQuantity < 1) return;

        try {
            // The API uses addItem to update quantity
            await cartApi.addItem(sku, newQuantity);
            fetchCart();
            showToast('success', 'Cart updated');
        } catch (err) {
            console.error('Failed to update quantity:', err);
            showToast('error', getErrorMessage(err, 'Failed to update quantity'));
        }
    };

    // Place order
    const handlePlaceOrder = async () => {
        setOrderLoading(true);
        setOrderSuccess(null);

        try {
            const idempotencyKey = uuidv4();
            const order = await orderApi.place(idempotencyKey);
            setOrderSuccess(order);
            setCart(null); // Clear cart display
            showToast('success', 'Order placed successfully!');
        } catch (err) {
            console.error('Failed to place order:', err);
            const message = getErrorMessage(err, 'Failed to place order. Please try again.');
            showToast('error', message);
        } finally {
            setOrderLoading(false);
        }
    };

    // Calculate summary
    const items = cart?.items || [];
    const itemCount = items.reduce((sum, item) => sum + item.quantity, 0);
    const subtotal = cart?.subtotal || cart?.totalPrice || 0;
    const discountAmount = cart?.discountAmount || 0;
    const finalTotal = cart?.totalPrice || 0;

    if (loading) {
        return (
            <div className="page">
                <div className="container">
                    <div className="loading-page">
                        <div className="loading-spinner"></div>
                    </div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="page">
                <div className="container">
                    <div className="card" style={{ textAlign: 'center', padding: 'var(--spacing-2xl)' }}>
                        <p style={{ color: 'var(--color-error)', marginBottom: 'var(--spacing-md)' }}>{error}</p>
                        <button className="btn btn-primary" onClick={fetchCart}>
                            Try Again
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    // Order success screen
    if (orderSuccess) {
        return (
            <div className="page">
                <div className="container">
                    <div className="card" style={{ maxWidth: '600px', margin: '0 auto', textAlign: 'center', padding: 'var(--spacing-2xl)' }}>
                        <div style={{ fontSize: '5rem', marginBottom: 'var(--spacing-lg)' }}>🎉</div>
                        <h2 className="text-gradient" style={{ marginBottom: 'var(--spacing-md)' }}>
                            Order Placed Successfully!
                        </h2>
                        <p className="text-secondary" style={{ marginBottom: 'var(--spacing-lg)' }}>
                            Thank you for your order. Your order ID is:
                        </p>
                        <code style={{
                            display: 'block',
                            padding: 'var(--spacing-md)',
                            background: 'var(--color-bg-tertiary)',
                            borderRadius: 'var(--border-radius-md)',
                            marginBottom: 'var(--spacing-lg)',
                            wordBreak: 'break-all'
                        }}>
                            {orderSuccess.id}
                        </code>
                        <div style={{ display: 'flex', gap: 'var(--spacing-md)', justifyContent: 'center' }}>
                            <Link to="/products" className="btn btn-primary btn-lg">
                                Continue Shopping
                            </Link>
                        </div>

                        {/* Order details */}
                        <div style={{ marginTop: 'var(--spacing-xl)', textAlign: 'left' }}>
                            <h4 style={{ marginBottom: 'var(--spacing-md)' }}>Order Details</h4>
                            {orderSuccess.items?.map((item, idx) => (
                                <div
                                    key={idx}
                                    style={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        padding: 'var(--spacing-sm) 0',
                                        borderBottom: '1px solid var(--border-color)'
                                    }}
                                >
                                    <span>{item.name} × {item.quantity}</span>
                                    <span>${item.totalPrice?.toFixed(2)}</span>
                                </div>
                            ))}
                            <div style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                padding: 'var(--spacing-md) 0',
                                fontWeight: 700,
                                fontSize: 'var(--font-size-lg)'
                            }}>
                                <span>Total</span>
                                <span className="text-gradient">${orderSuccess.totalAmount?.toFixed(2)}</span>
                            </div>
                            {orderSuccess.discountAmount > 0 && (
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    paddingBottom: 'var(--spacing-md)',
                                    color: 'var(--color-success)',
                                    fontSize: 'var(--font-size-sm)'
                                }}>
                                    <span>Discount Applied</span>
                                    <span>-${orderSuccess.discountAmount?.toFixed(2)}</span>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    // Empty cart
    if (!items.length || cart?.empty) {
        return (
            <div className="page">
                <div className="container">
                    <div className="empty-cart">
                        <div className="empty-cart-icon">🛒</div>
                        <h2>Your cart is empty</h2>
                        <p className="text-secondary" style={{ marginBottom: 'var(--spacing-lg)' }}>
                            Looks like you haven't added any items to your cart yet.
                        </p>
                        <Link to="/products" className="btn btn-primary btn-lg">
                            Browse Products
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="page">
            <div className="container">
                <h1 style={{ marginBottom: 'var(--spacing-xl)' }}>Shopping Cart</h1>

                <div className="cart-container">
                    {/* Cart Items */}
                    <div className="cart-items">
                        {items.map((item) => (
                            <CartItem
                                key={item.productId}
                                item={item}
                                onUpdateQuantity={handleUpdateQuantity}
                            />
                        ))}
                    </div>

                    {/* Cart Summary */}
                    <div className="cart-summary">
                        <h3 className="cart-summary-title">Order Summary</h3>

                        <div className="cart-summary-row">
                            <span>Items ({itemCount})</span>
                            <span>${subtotal.toFixed(2)}</span>
                        </div>

                        <div className="cart-summary-row">
                            <span>Shipping</span>
                            <span className="text-gradient">FREE</span>
                        </div>

                        <div className="cart-summary-total">
                            <span>Subtotal</span>
                            <span>${subtotal.toFixed(2)}</span>
                        </div>

                        {discountAmount > 0 && (
                            <div className="cart-summary-row" style={{ color: 'var(--color-success)' }}>
                                <span>Discount (10%)</span>
                                <span>-${discountAmount.toFixed(2)}</span>
                            </div>
                        )}

                        <div className="cart-summary-total" style={{ marginTop: 'var(--spacing-sm)', paddingTop: 'var(--spacing-sm)', borderTop: '1px solid var(--border-color)' }}>
                            <span>Total</span>
                            <span>${finalTotal.toFixed(2)}</span>
                        </div>

                        <button
                            className="btn btn-primary btn-lg w-full"
                            style={{ marginTop: 'var(--spacing-lg)' }}
                            onClick={handlePlaceOrder}
                            disabled={orderLoading}
                        >
                            {orderLoading ? (
                                <>
                                    <span className="loading-spinner" style={{ width: 20, height: 20 }}></span>
                                    Processing...
                                </>
                            ) : (
                                'Place Order'
                            )}
                        </button>

                        <Link
                            to="/products"
                            className="btn btn-ghost w-full"
                            style={{ marginTop: 'var(--spacing-sm)' }}
                        >
                            Continue Shopping
                        </Link>
                    </div>
                </div>
            </div>

            {/* Toast notifications */}
            {toast && (
                <div className="toast-container">
                    <div className={`toast ${toast.type}`}>
                        {toast.type === 'success' && '✓ '}
                        {toast.type === 'error' && '✕ '}
                        {toast.message}
                    </div>
                </div>
            )}
        </div>
    );
}

export default Cart;
