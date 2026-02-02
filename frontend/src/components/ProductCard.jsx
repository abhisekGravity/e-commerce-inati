import { useState } from 'react';

function ProductCard({ product, onAddToCart }) {
    const [quantity, setQuantity] = useState(1);
    const [isAdding, setIsAdding] = useState(false);

    const { id, sku, name, price, inventory } = product;
    const inStock = inventory > 0;

    const handleQuantityChange = (delta) => {
        const newQuantity = quantity + delta;
        if (newQuantity >= 1 && newQuantity <= Math.max(inventory, 1)) {
            setQuantity(newQuantity);
        }
    };

    const handleAddToCart = async () => {
        setIsAdding(true);
        await onAddToCart(sku, quantity);
        setIsAdding(false);
        setQuantity(1);
    };

    // Generate a fun emoji based on product name
    const getProductEmoji = (name) => {
        const emojis = ['📱', '💻', '⌚', '🎧', '📷', '🖥️', '⌨️', '🖱️', '🎮', '📺'];
        const hash = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
        return emojis[hash % emojis.length];
    };

    return (
        <article className="product-card">
            <div className="product-image">
                {getProductEmoji(name)}
            </div>

            <div className="product-body">
                <span className="product-sku">{sku}</span>
                <h3 className="product-name">{name}</h3>
                <div className="product-price">${price?.toFixed(2)}</div>

                <div className="product-stock">
                    <span className={`stock-indicator ${inStock ? 'in-stock' : 'out-of-stock'}`}></span>
                    <span className={inStock ? '' : 'text-muted'}>
                        {inStock ? `${inventory} in stock` : 'Out of stock'}
                    </span>
                </div>

                <div className="product-actions">
                    <div className="quantity-control">
                        <button
                            onClick={() => handleQuantityChange(-1)}
                            disabled={quantity <= 1 || !inStock}
                        >
                            −
                        </button>
                        <span>{quantity}</span>
                        <button
                            onClick={() => handleQuantityChange(1)}
                            disabled={quantity >= inventory || !inStock}
                        >
                            +
                        </button>
                    </div>

                    <button
                        className="btn btn-primary"
                        style={{ flex: 1 }}
                        onClick={handleAddToCart}
                        disabled={!inStock || isAdding}
                    >
                        {isAdding ? 'Adding...' : 'Add to Cart'}
                    </button>
                </div>
            </div>
        </article>
    );
}

export default ProductCard;
