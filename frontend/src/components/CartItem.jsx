function CartItem({ item, onUpdateQuantity }) {
    const { productId, sku, name, quantity, unitPrice, baseUnitPrice, totalPrice } = item;

    // Generate a fun emoji based on product name
    const getProductEmoji = (name) => {
        const emojis = ['📱', '💻', '⌚', '🎧', '📷', '🖥️', '⌨️', '🖱️', '🎮', '📺'];
        const hash = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
        return emojis[hash % emojis.length];
    };

    const hasDiscount = baseUnitPrice && unitPrice < baseUnitPrice;
    const discountPercent = hasDiscount 
        ? Math.round(((baseUnitPrice - unitPrice) / baseUnitPrice) * 100) 
        : 0;

    return (
        <div className="cart-item">
            <div className="cart-item-image">
                {getProductEmoji(name)}
            </div>

            <div className="cart-item-details">
                <h4 className="cart-item-name">{name}</h4>
                <span className="cart-item-sku">{sku}</span>
                <div className="cart-item-price">
                     {hasDiscount ? (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                            <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                                <span style={{ 
                                    textDecoration: 'line-through', 
                                    color: 'var(--text-secondary)', 
                                    fontSize: '0.9em' 
                                }}>
                                    ${baseUnitPrice?.toFixed(2)}
                                </span>
                                <span style={{ 
                                    color: 'var(--color-success)', 
                                    fontSize: '0.8em',
                                    fontWeight: 'bold',
                                    background: 'rgba(var(--color-success-rgb), 0.1)',
                                    padding: '2px 6px',
                                    borderRadius: '4px'
                                }}>
                                    {discountPercent}% OFF
                                </span>
                            </div>
                            <span style={{ fontWeight: 600 }}>${unitPrice?.toFixed(2)} each</span>
                        </div>
                     ) : (
                        <span>${unitPrice?.toFixed(2)} each</span>
                     )}
                </div>
            </div>

            <div className="quantity-control">
                <button
                    onClick={() => onUpdateQuantity(sku, quantity - 1)}
                    disabled={quantity <= 1}
                >
                    −
                </button>
                <span>{quantity}</span>
                <button onClick={() => onUpdateQuantity(sku, quantity + 1)}>
                    +
                </button>
            </div>

            <div style={{ minWidth: '100px', textAlign: 'right' }}>
                <div style={{ fontWeight: 700, fontSize: 'var(--font-size-lg)' }}>
                    ${totalPrice?.toFixed(2)}
                </div>
            </div>
        </div>
    );
}

export default CartItem;
