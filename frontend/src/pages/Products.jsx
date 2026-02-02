import { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { productApi, cartApi } from '../api/api';
import ProductCard from '../components/ProductCard';

function Products() {
    const { isAuthenticated } = useAuth();

    // Product state
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Pagination state
    const [totalPages, setTotalPages] = useState(1);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Filter state
    const [filters, setFilters] = useState({
        name: '',
        minPrice: '',
        maxPrice: '',
        inStock: false,
        sortBy: 'PRICE',
        direction: 'ASC',
        limit: 12,
    });

    // Toast state
    const [toast, setToast] = useState(null);

    // Fetch products
    const fetchProducts = useCallback(async () => {
        setLoading(true);
        setError(null);

        try {
            const params = {
                ...filters,
                offset: currentPage * filters.limit,
            };

            // Clean up empty params
            if (!params.name) delete params.name;
            if (!params.minPrice) delete params.minPrice;
            if (!params.maxPrice) delete params.maxPrice;
            if (!params.inStock) delete params.inStock;

            const response = await productApi.list(params);
            setProducts(response.content || []);
            setTotalPages(response.totalPages || 1);
            setTotalElements(response.totalElements || 0);
        } catch (err) {
            console.error('Failed to fetch products:', err);
            setError('Failed to load products. Please try again.');
        } finally {
            setLoading(false);
        }
    }, [filters, currentPage]);

    // Load products on mount and when filters/page change
    useEffect(() => {
        fetchProducts();
    }, [fetchProducts]);

    // Handle filter changes
    const handleFilterChange = (key, value) => {
        setFilters((prev) => ({ ...prev, [key]: value }));
        setCurrentPage(0); // Reset to first page on filter change
    };

    // Clear all filters
    const clearFilters = () => {
        setFilters({
            name: '',
            minPrice: '',
            maxPrice: '',
            inStock: false,
            sortBy: 'PRICE',
            direction: 'ASC',
            limit: 12,
        });
        setCurrentPage(0);
    };

    // Add to cart handler
    const handleAddToCart = async (sku, quantity) => {
        if (!isAuthenticated) {
            setToast({ type: 'warning', message: 'Please login to add items to cart' });
            setTimeout(() => setToast(null), 3000);
            return;
        }

        try {
            await cartApi.addItem(sku, quantity);
            setToast({ type: 'success', message: `Added ${quantity} item(s) to cart!` });
            setTimeout(() => setToast(null), 3000);
        } catch (err) {
            console.error('Failed to add to cart:', err);
            const message = err.response?.data?.message || 'Failed to add item to cart';
            setToast({ type: 'error', message });
            setTimeout(() => setToast(null), 3000);
        }
    };

    // Pagination controls
    const goToPage = (page) => {
        setCurrentPage(page);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    return (
        <div className="page">
            <div className="container">
                <div style={{ display: 'grid', gridTemplateColumns: '280px 1fr', gap: 'var(--spacing-xl)' }}>
                    {/* Filters Sidebar */}
                    <aside className="filters-sidebar">
                        <div className="filter-section">
                            <h3 className="filter-title">Search</h3>
                            <input
                                type="text"
                                className="form-input"
                                placeholder="Search by name..."
                                value={filters.name}
                                onChange={(e) => handleFilterChange('name', e.target.value)}
                            />
                        </div>

                        <div className="filter-section">
                            <h3 className="filter-title">Price Range</h3>
                            <div className="filter-range">
                                <input
                                    type="number"
                                    className="form-input"
                                    placeholder="Min"
                                    value={filters.minPrice}
                                    onChange={(e) => handleFilterChange('minPrice', e.target.value)}
                                />
                                <span>-</span>
                                <input
                                    type="number"
                                    className="form-input"
                                    placeholder="Max"
                                    value={filters.maxPrice}
                                    onChange={(e) => handleFilterChange('maxPrice', e.target.value)}
                                />
                            </div>
                        </div>

                        <div className="filter-section">
                            <h3 className="filter-title">Availability</h3>
                            <label className="filter-checkbox-label">
                                <input
                                    type="checkbox"
                                    className="form-checkbox"
                                    checked={filters.inStock}
                                    onChange={(e) => handleFilterChange('inStock', e.target.checked)}
                                />
                                In Stock Only
                            </label>
                        </div>

                        <div className="filter-section">
                            <h3 className="filter-title">Sort By</h3>
                            <select
                                className="form-input form-select"
                                value={filters.sortBy}
                                onChange={(e) => handleFilterChange('sortBy', e.target.value)}
                            >
                                <option value="PRICE">Price</option>
                                <option value="NAME">Name</option>
                                <option value="INVENTORY">Inventory</option>
                            </select>
                            <div style={{ marginTop: 'var(--spacing-sm)', display: 'flex', gap: 'var(--spacing-sm)' }}>
                                <button
                                    className={`btn btn-sm ${filters.direction === 'ASC' ? 'btn-primary' : 'btn-secondary'}`}
                                    onClick={() => handleFilterChange('direction', 'ASC')}
                                >
                                    Low to High
                                </button>
                                <button
                                    className={`btn btn-sm ${filters.direction === 'DESC' ? 'btn-primary' : 'btn-secondary'}`}
                                    onClick={() => handleFilterChange('direction', 'DESC')}
                                >
                                    High to Low
                                </button>
                            </div>
                        </div>

                        <button className="btn btn-secondary w-full" onClick={clearFilters}>
                            Clear Filters
                        </button>
                    </aside>

                    {/* Products Grid */}
                    <main>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 'var(--spacing-lg)' }}>
                            <h1>Products</h1>
                            <span className="text-secondary">
                                {totalElements} product{totalElements !== 1 ? 's' : ''} found
                            </span>
                        </div>

                        {loading ? (
                            <div className="loading-page">
                                <div className="loading-spinner"></div>
                            </div>
                        ) : error ? (
                            <div className="card" style={{ textAlign: 'center', padding: 'var(--spacing-2xl)' }}>
                                <p style={{ color: 'var(--color-error)', marginBottom: 'var(--spacing-md)' }}>{error}</p>
                                <button className="btn btn-primary" onClick={fetchProducts}>
                                    Try Again
                                </button>
                            </div>
                        ) : products.length === 0 ? (
                            <div className="card" style={{ textAlign: 'center', padding: 'var(--spacing-2xl)' }}>
                                <div style={{ fontSize: '4rem', marginBottom: 'var(--spacing-md)' }}>📦</div>
                                <h3>No products found</h3>
                                <p className="text-secondary">Try adjusting your filters</p>
                            </div>
                        ) : (
                            <>
                                <div className="grid grid-3">
                                    {products.map((product) => (
                                        <ProductCard
                                            key={product.id}
                                            product={product}
                                            onAddToCart={handleAddToCart}
                                        />
                                    ))}
                                </div>

                                {/* Pagination */}
                                {totalPages > 1 && (
                                    <div className="pagination">
                                        <button
                                            className="pagination-btn"
                                            onClick={() => goToPage(currentPage - 1)}
                                            disabled={currentPage === 0}
                                        >
                                            ←
                                        </button>

                                        {[...Array(totalPages)].map((_, idx) => (
                                            <button
                                                key={idx}
                                                className={`pagination-btn ${idx === currentPage ? 'active' : ''}`}
                                                onClick={() => goToPage(idx)}
                                            >
                                                {idx + 1}
                                            </button>
                                        ))}

                                        <button
                                            className="pagination-btn"
                                            onClick={() => goToPage(currentPage + 1)}
                                            disabled={currentPage === totalPages - 1}
                                        >
                                            →
                                        </button>
                                    </div>
                                )}
                            </>
                        )}
                    </main>
                </div>
            </div>

            {/* Toast notifications */}
            {toast && (
                <div className="toast-container">
                    <div className={`toast ${toast.type}`}>
                        {toast.type === 'success' && '✓ '}
                        {toast.type === 'error' && '✕ '}
                        {toast.type === 'warning' && '⚠ '}
                        {toast.message}
                    </div>
                </div>
            )}
        </div>
    );
}

export default Products;
