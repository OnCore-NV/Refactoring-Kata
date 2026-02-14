import React from 'react';
import { ShoppingCart } from '../../model/ShoppingCart';
import { SupermarketCatalog } from '../../model/SupermarketCatalog';
import { ProductUnit } from '../../model/ProductUnit';

interface Props {
  cart: ShoppingCart;
  catalog: SupermarketCatalog;
  onCheckout: () => void;
  onClear: () => void;
}

const Cart: React.FC<Props> = ({ cart, catalog, onCheckout, onClear }) => {
  const items = cart.getItems();
  const totalItems = items.reduce((sum, item) => sum + item.quantity, 0);

  const calculateSubtotal = () => {
    return items.reduce((sum, item) => {
      const unitPrice = catalog.getUnitPrice(item.product);
      return sum + (unitPrice * item.quantity);
    }, 0);
  };

  return (
    <div className="cart">
      <h2>Shopping Cart ({totalItems} items)</h2>
      
      {items.length === 0 ? (
        <p className="empty-cart">Your cart is empty</p>
      ) : (
        <>
          <div className="cart-items">
            {items.map((item, index) => {
              const unitPrice = catalog.getUnitPrice(item.product);
              const total = unitPrice * item.quantity;
              
              return (
                <div key={`${item.product.name}-${index}`} className="cart-item">
                  <div className="cart-item-name">{item.product.name}</div>
                  <div className="cart-item-details">
                    <span>
                      {item.quantity.toFixed(item.product.unit === ProductUnit.Kilo ? 3 : 0)}
                      {item.product.unit === ProductUnit.Kilo ? ' kg' : ''}
                    </span>
                    <span>× €{unitPrice.toFixed(2)}</span>
                    <span className="cart-item-total">€{total.toFixed(2)}</span>
                  </div>
                </div>
              );
            })}
          </div>
          
          <div className="cart-subtotal">
            <strong>Subtotal: €{calculateSubtotal().toFixed(2)}</strong>
          </div>
          
          <div className="cart-actions">
            <button onClick={onCheckout} className="checkout-button">
              Checkout
            </button>
            <button onClick={onClear} className="clear-button">
              Clear Cart
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default Cart;
