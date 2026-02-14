import React, { useState } from 'react';
import { Product } from '../../model/Product';
import { ProductUnit } from '../../model/ProductUnit';

interface ProductWithPrice {
  product: Product;
  price: number;
}

interface Props {
  products: ProductWithPrice[];
  onAddToCart: (product: Product, quantity: number) => void;
}

const ProductCatalog: React.FC<Props> = ({ products, onAddToCart }) => {
  const [quantities, setQuantities] = useState<{ [key: string]: number }>({});

  const handleQuantityChange = (productName: string, value: string) => {
    const quantity = parseFloat(value) || 0;
    setQuantities({ ...quantities, [productName]: quantity });
  };

  const handleAdd = (product: Product) => {
    const quantity = quantities[product.name] || 1;
    if (quantity > 0) {
      onAddToCart(product, quantity);
      setQuantities({ ...quantities, [product.name]: 0 });
    }
  };

  return (
    <div className="product-catalog">
      <h2>Product Catalog</h2>
      <div className="special-offers-info">
        <h3>🎁 Special Offers This Week:</h3>
        <ul>
          <li>Toothbrush: Buy 3 for the price of 2</li>
          <li>Toothpaste: 5 for €7.49</li>
          <li>Apples: 20% off</li>
          <li>Rice: 10% off</li>
          <li>Cherry Tomatoes: 2 for €0.99</li>
        </ul>
      </div>
      
      <div className="products-list">
        {products.map(({ product, price }) => (
          <div key={product.name} className="product-item">
            <div className="product-info">
              <h3>{product.name}</h3>
              <p className="price">
                €{price.toFixed(2)}
                {product.unit === ProductUnit.Kilo ? '/kg' : ' each'}
              </p>
            </div>
            <div className="product-actions">
              <input
                type="number"
                min="0"
                step={product.unit === ProductUnit.Kilo ? '0.1' : '1'}
                value={quantities[product.name] || ''}
                onChange={(e) => handleQuantityChange(product.name, e.target.value)}
                placeholder="Qty"
                className="quantity-input"
              />
              <button 
                onClick={() => handleAdd(product)}
                className="add-button"
              >
                Add to Cart
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ProductCatalog;
