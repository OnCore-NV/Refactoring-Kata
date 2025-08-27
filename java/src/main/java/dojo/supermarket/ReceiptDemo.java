package dojo.supermarket;

import dojo.supermarket.model.*;

public class ReceiptDemo {
    
    static class FakeCatalog implements SupermarketCatalog {
        private java.util.Map<String, Product> products = new java.util.HashMap<>();
        private java.util.Map<String, Double> prices = new java.util.HashMap<>();

        @Override
        public void addProduct(Product product, double price) {
            this.products.put(product.getName(), product);
            this.prices.put(product.getName(), price);
        }

        @Override
        public double getUnitPrice(Product p) {
            return this.prices.get(p.getName());
        }
    }
    
    public static void main(String[] args) {
        // Set up sample data
        SupermarketCatalog catalog = new FakeCatalog();
        Product apples = new Product("apples", ProductUnit.KILO);
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        Product toothpaste = new Product("toothpaste", ProductUnit.EACH);
        
        catalog.addProduct(apples, 1.99);
        catalog.addProduct(toothbrush, 0.99);
        catalog.addProduct(toothpaste, 1.79);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);
        cart.addItemQuantity(toothbrush, 2);
        cart.addItemQuantity(toothpaste, 1);

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Print text receipt
        ReceiptPrinter textPrinter = new ReceiptPrinter();
        System.out.println("=== TEXT RECEIPT ===");
        System.out.println(textPrinter.printReceipt(receipt));

        // Print HTML receipt
        HtmlReceiptPrinter htmlPrinter = new HtmlReceiptPrinter();
        System.out.println("\n=== HTML RECEIPT ===");
        System.out.println(htmlPrinter.printReceipt(receipt));
    }
}