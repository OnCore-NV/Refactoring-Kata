import React, { useState } from 'react';
import { Product } from '../../model/Product';
import { ProductUnit } from '../../model/ProductUnit';
import { InMemoryCatalog } from '../InMemoryCatalog';
import { Teller } from '../../model/Teller';
import { ShoppingCart } from '../../model/ShoppingCart';
import { SpecialOfferType } from '../../model/SpecialOfferType';
import { ReceiptPrinter } from '../../ReceiptPrinter';
import ProductCatalog from './ProductCatalog';
import Cart from './Cart';
import ReceiptDisplay from './ReceiptDisplay';
import './App.css';

interface ProductWithPrice {
  product: Product;
  price: number;
}

const App: React.FC = () => {
  const [catalog] = useState(() => {
    const cat = new InMemoryCatalog();
    
    // Add products
    const toothbrush = new Product('Toothbrush', ProductUnit.Each);
    const toothpaste = new Product('Toothpaste', ProductUnit.Each);
    const apples = new Product('Apples', ProductUnit.Kilo);
    const rice = new Product('Rice', ProductUnit.Each);
    const cherryTomatoes = new Product('Cherry Tomatoes', ProductUnit.Each);
    const milk = new Product('Milk', ProductUnit.Each);
    
    cat.addProduct(toothbrush, 0.99);
    cat.addProduct(toothpaste, 1.79);
    cat.addProduct(apples, 1.99);
    cat.addProduct(rice, 2.49);
    cat.addProduct(cherryTomatoes, 0.69);
    cat.addProduct(milk, 1.29);
    
    return cat;
  });

  const [teller] = useState(() => {
    const t = new Teller(catalog);
    
    // Add special offers
    const toothbrush = new Product('Toothbrush', ProductUnit.Each);
    const toothpaste = new Product('Toothpaste', ProductUnit.Each);
    const apples = new Product('Apples', ProductUnit.Kilo);
    const rice = new Product('Rice', ProductUnit.Each);
    const cherryTomatoes = new Product('Cherry Tomatoes', ProductUnit.Each);
    
    t.addSpecialOffer(SpecialOfferType.ThreeForTwo, toothbrush, 0);
    t.addSpecialOffer(SpecialOfferType.FiveForAmount, toothpaste, 7.49);
    t.addSpecialOffer(SpecialOfferType.TenPercentDiscount, apples, 20);
    t.addSpecialOffer(SpecialOfferType.TenPercentDiscount, rice, 10);
    t.addSpecialOffer(SpecialOfferType.TwoForAmount, cherryTomatoes, 0.99);
    
    return t;
  });

  const [cart, setCart] = useState<ShoppingCart>(new ShoppingCart());
  const [receiptText, setReceiptText] = useState<string>('');

  const products: ProductWithPrice[] = catalog.getAllProducts().map(p => ({
    product: p,
    price: catalog.getUnitPrice(p)
  }));

  const handleAddToCart = (product: Product, quantity: number) => {
    const newCart = new ShoppingCart();
    // Copy existing items
    for (const item of cart.getItems()) {
      newCart.addItemQuantity(item.product, item.quantity);
    }
    // Add new item
    newCart.addItemQuantity(product, quantity);
    setCart(newCart);
  };

  const handleCheckout = () => {
    const receipt = teller.checksOutArticlesFrom(cart);
    const printer = new ReceiptPrinter(40);
    const text = printer.printReceipt(receipt);
    setReceiptText(text);
  };

  const handleClearCart = () => {
    setCart(new ShoppingCart());
    setReceiptText('');
  };

  return (
    <div className="app">
      <header className="app-header">
        <h1>🛒 Supermarket</h1>
      </header>
      
      <div className="app-content">
        <div className="left-panel">
          <ProductCatalog 
            products={products} 
            onAddToCart={handleAddToCart}
          />
        </div>
        
        <div className="right-panel">
          <Cart 
            cart={cart}
            catalog={catalog}
            onCheckout={handleCheckout}
            onClear={handleClearCart}
          />
          
          {receiptText && (
            <ReceiptDisplay receiptText={receiptText} />
          )}
        </div>
      </div>
    </div>
  );
};

export default App;
