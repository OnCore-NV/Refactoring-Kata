package dojo.supermarket;

import dojo.supermarket.model.*;

import java.io.FileWriter;
import java.io.IOException;

public class HtmlReceiptDemo {
    public static void main(String[] args) throws IOException {
        // Set up sample data with multiple items and discounts
        SupermarketCatalog catalog = new ReceiptDemo.FakeCatalog();
        
        Product apples = new Product("apples", ProductUnit.KILO);
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        Product toothpaste = new Product("toothpaste", ProductUnit.EACH);
        Product cherryTomatoes = new Product("cherry tomatoes", ProductUnit.EACH);
        
        catalog.addProduct(apples, 1.99);
        catalog.addProduct(toothbrush, 0.99);
        catalog.addProduct(toothpaste, 1.79);
        catalog.addProduct(cherryTomatoes, 0.69);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0);
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT, cherryTomatoes, 0.99);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);
        cart.addItemQuantity(toothbrush, 3);
        cart.addItemQuantity(toothpaste, 2);
        cart.addItemQuantity(cherryTomatoes, 2); // Buy 2 for 0.99 deal

        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // Generate HTML receipt
        HtmlReceiptPrinter htmlPrinter = new HtmlReceiptPrinter();
        String htmlReceipt = htmlPrinter.printReceipt(receipt);

        // Write to file
        try (FileWriter writer = new FileWriter("/tmp/sample_receipt.html")) {
            writer.write(htmlReceipt);
            System.out.println("HTML receipt written to /tmp/sample_receipt.html");
        }

        // Also show comparison
        ReceiptPrinter textPrinter = new ReceiptPrinter();
        System.out.println("\n=== TEXT RECEIPT ===");
        System.out.println(textPrinter.printReceipt(receipt));
        
        System.out.println("\n=== HTML RECEIPT ===");
        System.out.println(htmlReceipt);
    }
}