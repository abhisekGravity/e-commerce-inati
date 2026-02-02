function CartItem({ item, onUpdateQuantity }) {
    const { productId, sku, name, quantity, unitPrice, totalPrice } = item;

    // Generate a fun emoji based on product name
    const getProductEmoji = (name) => {
        const emojis = ['📱', '💻', '⌚', '🎧', '📷', '🖥️', '⌨️', '🖱️', '🎮', '📺'];
        const hash = name.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
        return emojis[hash % emojis.length];
    };

    return (
        <div className="cart-item">
            <div className="cart-item-image">
                {getProductEmoji(name)}
            </div>

            <div className="cart-item-details">
                <h4 className="cart-item-name">{name}</h4>
                <span className="cart-item-sku">{sku}</span>
                <div className="cart-item-price">${unitPrice?.toFixed(2)} each</div>
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
