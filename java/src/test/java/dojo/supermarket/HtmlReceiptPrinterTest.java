package dojo.supermarket;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HtmlReceiptPrinterTest {

    @Test
    void printHtmlReceiptWithSingleItem() {
        // Arrange
        SupermarketCatalog catalog = new FakeCatalog();
        Product apples = new Product("apples", ProductUnit.KILO);
        catalog.addProduct(apples, 1.99);

        Teller teller = new Teller(catalog);
        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);

        Receipt receipt = teller.checksOutArticlesFrom(cart);
        HtmlReceiptPrinter printer = new HtmlReceiptPrinter();

        // Act
        String htmlReceipt = printer.printReceipt(receipt);

        // Assert
        assertTrue(htmlReceipt.contains("<html>"));
        assertTrue(htmlReceipt.contains("apples"));
        assertTrue(htmlReceipt.contains("4.98")); // 2.5 * 1.99 = 4.975 rounded to 4.98
        assertTrue(htmlReceipt.contains("1.99"));
        assertTrue(htmlReceipt.contains("2.500"));
        assertTrue(htmlReceipt.contains("Total"));
        assertTrue(htmlReceipt.contains("</html>"));
    }

    @Test 
    void printHtmlReceiptWithDiscount() {
        // Arrange
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 3); // Buy 3 to trigger discount

        Receipt receipt = teller.checksOutArticlesFrom(cart);
        HtmlReceiptPrinter printer = new HtmlReceiptPrinter();

        // Act
        String htmlReceipt = printer.printReceipt(receipt);

        // Assert
        assertTrue(htmlReceipt.contains("<html>"));
        assertTrue(htmlReceipt.contains("toothbrush"));
        assertTrue(htmlReceipt.contains("2.97")); // 3 * 0.99
        assertTrue(htmlReceipt.contains("0.99"));
        assertTrue(htmlReceipt.contains("3"));
        assertTrue(htmlReceipt.contains("10.0% off"));
        assertTrue(htmlReceipt.contains("Total"));
        assertTrue(htmlReceipt.contains("</html>"));
    }

    @Test
    void htmlReceiptShouldHaveSameDataAsTextReceipt() {
        // Arrange
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 2);

        Receipt receipt = teller.checksOutArticlesFrom(cart);
        
        ReceiptPrinter textPrinter = new ReceiptPrinter();
        HtmlReceiptPrinter htmlPrinter = new HtmlReceiptPrinter();

        // Act
        String textReceipt = textPrinter.printReceipt(receipt);
        String htmlReceipt = htmlPrinter.printReceipt(receipt);

        // Assert - Both should contain the same prices and data
        assertTrue(textReceipt.contains("1.98")); // 2 * 0.99
        assertTrue(htmlReceipt.contains("1.98"));
        
        assertTrue(textReceipt.contains("0.99"));
        assertTrue(htmlReceipt.contains("0.99"));
        
        assertTrue(textReceipt.contains("toothbrush"));
        assertTrue(htmlReceipt.contains("toothbrush"));
        
        if (receipt.getDiscounts().size() > 0) {
            String discountAmount = String.format("%.2f", Math.abs(receipt.getDiscounts().get(0).getDiscountAmount()));
            assertTrue(textReceipt.contains(discountAmount));
            assertTrue(htmlReceipt.contains(discountAmount));
        }
    }
}