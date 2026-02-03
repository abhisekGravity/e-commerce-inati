import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { productApi, tenantApi, getErrorMessage } from '../api/api';

function Admin() {
    const { isAuthenticated, fetchTenants } = useAuth();

    // Product form state
    const [productForm, setProductForm] = useState({
        sku: '',
        name: '',
        basePrice: '',
        inventory: '',
    });
    const [productLoading, setProductLoading] = useState(false);
    const [productMessage, setProductMessage] = useState(null);

    // Tenant form state
    const [tenantForm, setTenantForm] = useState({
        name: '',
    });
    const [tenantLoading, setTenantLoading] = useState(false);
    const [tenantMessage, setTenantMessage] = useState(null);

    // Handle product form changes
    const handleProductChange = (e) => {
        const { name, value } = e.target;
        setProductForm((prev) => ({ ...prev, [name]: value }));
    };

    // Handle tenant form changes
    const handleTenantChange = (e) => {
        const { name, value } = e.target;
        setTenantForm((prev) => ({ ...prev, [name]: value }));
    };

    // Create product
    const handleCreateProduct = async (e) => {
        e.preventDefault();
        setProductMessage(null);

        // Validation
        if (!productForm.sku || !productForm.name || !productForm.basePrice) {
            setProductMessage({ type: 'error', text: 'Please fill in all required fields' });
            return;
        }

        setProductLoading(true);

        try {
            const product = await productApi.create({
                sku: productForm.sku,
                name: productForm.name,
                basePrice: parseFloat(productForm.basePrice),
                inventory: parseInt(productForm.inventory) || 0,
            });

            setProductMessage({ type: 'success', text: `Product "${product.name}" created successfully!` });
            setProductForm({ sku: '', name: '', basePrice: '', inventory: '' });
        } catch (err) {
            console.error('Failed to create product:', err);
            const message = getErrorMessage(err, 'Failed to create product. Please try again.');
            setProductMessage({ type: 'error', text: message });
        } finally {
            setProductLoading(false);
        }
    };

    // Create tenant
    const handleCreateTenant = async (e) => {
        e.preventDefault();
        setTenantMessage(null);

        // Validation
        if (!tenantForm.name || tenantForm.name.length < 3 || tenantForm.name.length > 10) {
            setTenantMessage({ type: 'error', text: 'Tenant name must be 3-10 characters' });
            return;
        }

        setTenantLoading(true);

        try {
            const tenant = await tenantApi.create(tenantForm.name);
            setTenantMessage({ type: 'success', text: `Tenant "${tenant.name}" created! Slug: ${tenant.tenantSlug}` });
            setTenantForm({ name: '' });
            fetchTenants(); // Refresh tenant list
        } catch (err) {
            console.error('Failed to create tenant:', err);
            const message = getErrorMessage(err, 'Failed to create tenant. Please try again.');
            setTenantMessage({ type: 'error', text: message });
        } finally {
            setTenantLoading(false);
        }
    };

    if (!isAuthenticated) {
        return (
            <div className="page">
                <div className="container">
                    <div className="card" style={{ textAlign: 'center', padding: 'var(--spacing-2xl)' }}>
                        <div style={{ fontSize: '4rem', marginBottom: 'var(--spacing-lg)' }}>🔒</div>
                        <h3>Authentication Required</h3>
                        <p className="text-secondary">Please login to access the admin panel</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="page">
            <div className="container">
                <h1 style={{ marginBottom: 'var(--spacing-xl)' }}>Admin Panel</h1>

                <div className="admin-grid">
                    {/* Create Product Section */}
                    <section className="admin-section">
                        <h2 className="admin-section-title">
                            📦 Create Product
                        </h2>

                        <form onSubmit={handleCreateProduct}>
                            <div className="form-group">
                                <label className="form-label" htmlFor="sku">
                                    SKU *
                                </label>
                                <input
                                    type="text"
                                    id="sku"
                                    name="sku"
                                    className="form-input"
                                    placeholder="e.g., SKU-001"
                                    value={productForm.sku}
                                    onChange={handleProductChange}
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label" htmlFor="productName">
                                    Product Name *
                                </label>
                                <input
                                    type="text"
                                    id="productName"
                                    name="name"
                                    className="form-input"
                                    placeholder="e.g., Premium Headphones"
                                    value={productForm.name}
                                    onChange={handleProductChange}
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label" htmlFor="basePrice">
                                    Base Price ($) *
                                </label>
                                <input
                                    type="number"
                                    id="basePrice"
                                    name="basePrice"
                                    className="form-input"
                                    placeholder="0.00"
                                    min="0.01"
                                    step="0.01"
                                    value={productForm.basePrice}
                                    onChange={handleProductChange}
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label" htmlFor="inventory">
                                    Inventory
                                </label>
                                <input
                                    type="number"
                                    id="inventory"
                                    name="inventory"
                                    className="form-input"
                                    placeholder="0"
                                    min="0"
                                    value={productForm.inventory}
                                    onChange={handleProductChange}
                                />
                            </div>

                            {productMessage && (
                                <div
                                    className={productMessage.type === 'error' ? 'form-error' : ''}
                                    style={{
                                        padding: 'var(--spacing-md)',
                                        borderRadius: 'var(--border-radius-md)',
                                        marginBottom: 'var(--spacing-md)',
                                        background: productMessage.type === 'success'
                                            ? 'rgba(16, 185, 129, 0.1)'
                                            : 'rgba(239, 68, 68, 0.1)',
                                        color: productMessage.type === 'success'
                                            ? 'var(--color-success)'
                                            : 'var(--color-error)',
                                    }}
                                >
                                    {productMessage.type === 'success' ? '✓ ' : '✕ '}
                                    {productMessage.text}
                                </div>
                            )}

                            <button
                                type="submit"
                                className="btn btn-primary w-full"
                                disabled={productLoading}
                            >
                                {productLoading ? 'Creating...' : 'Create Product'}
                            </button>
                        </form>
                    </section>

                    {/* Create Tenant Section */}
                    <section className="admin-section">
                        <h2 className="admin-section-title">
                            🏪 Create Tenant Store
                        </h2>

                        <form onSubmit={handleCreateTenant}>
                            <div className="form-group">
                                <label className="form-label" htmlFor="tenantName">
                                    Store Name (3-10 characters) *
                                </label>
                                <input
                                    type="text"
                                    id="tenantName"
                                    name="name"
                                    className="form-input"
                                    placeholder="e.g., My Store"
                                    minLength={3}
                                    maxLength={10}
                                    value={tenantForm.name}
                                    onChange={handleTenantChange}
                                />
                            </div>

                            <div className="card" style={{ marginBottom: 'var(--spacing-lg)', padding: 'var(--spacing-md)' }}>
                                <p className="text-secondary" style={{ fontSize: 'var(--font-size-sm)' }}>
                                    💡 A tenant represents a separate store with its own products and customers.
                                    A unique slug will be generated automatically.
                                </p>
                            </div>

                            {tenantMessage && (
                                <div
                                    className={tenantMessage.type === 'error' ? 'form-error' : ''}
                                    style={{
                                        padding: 'var(--spacing-md)',
                                        borderRadius: 'var(--border-radius-md)',
                                        marginBottom: 'var(--spacing-md)',
                                        background: tenantMessage.type === 'success'
                                            ? 'rgba(16, 185, 129, 0.1)'
                                            : 'rgba(239, 68, 68, 0.1)',
                                        color: tenantMessage.type === 'success'
                                            ? 'var(--color-success)'
                                            : 'var(--color-error)',
                                    }}
                                >
                                    {tenantMessage.type === 'success' ? '✓ ' : '✕ '}
                                    {tenantMessage.text}
                                </div>
                            )}

                            <button
                                type="submit"
                                className="btn btn-primary w-full"
                                disabled={tenantLoading}
                            >
                                {tenantLoading ? 'Creating...' : 'Create Tenant'}
                            </button>
                        </form>
                    </section>
                </div>
            </div>
        </div>
    );
}

export default Admin;
