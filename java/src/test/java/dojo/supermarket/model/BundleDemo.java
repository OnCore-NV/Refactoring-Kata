package dojo.supermarket.model;

import dojo.supermarket.ReceiptPrinter;
import java.util.Arrays;
import java.util.List;

public class BundleDemo {
    public static void main(String[] args) {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);
        Product toothpaste = new Product("toothpaste", ProductUnit.EACH);
        catalog.addProduct(toothpaste, 1.79);

        Teller teller = new Teller(catalog);
        List<Product> bundleProducts = Arrays.asList(toothbrush, toothpaste);
        teller.addBundleOffer(bundleProducts, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1.0);
        cart.addItemQuantity(toothpaste, 1.0);
        
        Receipt receipt = teller.checksOutArticlesFrom(cart);
        
        String receiptText = new ReceiptPrinter().printReceipt(receipt);
        System.out.println(receiptText);
        System.out.println("Total Price: €" + String.format("%.2f", receipt.getTotalPrice()));
    }
}