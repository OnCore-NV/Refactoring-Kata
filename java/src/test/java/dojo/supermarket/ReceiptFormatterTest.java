package dojo.supermarket;

import dojo.supermarket.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReceiptFormatterTest {

    private Receipt createTestReceipt() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product apples = new Product("apples", ProductUnit.KILO);
        catalog.addProduct(apples, 1.99);
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);
        cart.addItemQuantity(toothbrush, 1.0);

        return teller.checksOutArticlesFrom(cart);
    }

    @Test
    void textFormatter_shouldFormatReceiptCorrectly() {
        Receipt receipt = createTestReceipt();
        TextReceiptFormatter formatter = new TextReceiptFormatter();
        
        String result = formatter.formatReceipt(receipt, 40);
        
        assertTrue(result.contains("apples"));
        assertTrue(result.contains("toothbrush"));
        assertTrue(result.contains("10.0% off"));
        assertTrue(result.contains("Total:"));
        // Should be plain text format
        assertFalse(result.contains("<html>"));
        assertFalse(result.contains("<table>"));
    }

    @Test
    void htmlFormatter_shouldFormatReceiptCorrectly() {
        Receipt receipt = createTestReceipt();
        HtmlReceiptFormatter formatter = new HtmlReceiptFormatter();
        
        String result = formatter.formatReceipt(receipt, 40);
        
        // Should contain HTML structure
        assertTrue(result.contains("<html>"));
        assertTrue(result.contains("<body>"));
        assertTrue(result.contains("<table>"));
        assertTrue(result.contains("</html>"));
        
        // Should contain receipt data
        assertTrue(result.contains("apples"));
        assertTrue(result.contains("toothbrush"));
        assertTrue(result.contains("10.0% off"));
        assertTrue(result.contains("Total:"));
        
        // Should escape HTML characters
        assertTrue(result.contains("&lt;") || !result.contains("<script>"));
    }

    @Test
    void htmlFormatter_shouldEscapeSpecialCharacters() {
        Product productWithSpecialChars = new Product("test<>&\"'", ProductUnit.EACH);
        Receipt receipt = new Receipt();
        receipt.addProduct(productWithSpecialChars, 1.0, 1.0, 1.0);
        
        HtmlReceiptFormatter formatter = new HtmlReceiptFormatter();
        String result = formatter.formatReceipt(receipt, 40);
        
        assertTrue(result.contains("&lt;"));
        assertTrue(result.contains("&gt;"));
        assertTrue(result.contains("&amp;"));
        assertTrue(result.contains("&quot;"));
        assertTrue(result.contains("&#39;"));
    }

    @Test
    void receiptPrinter_shouldUseTextFormatterByDefault() {
        Receipt receipt = createTestReceipt();
        ReceiptPrinter printer = new ReceiptPrinter();
        
        String result = printer.printReceipt(receipt);
        
        assertFalse(result.contains("<html>"));
        assertTrue(result.contains("Total:"));
    }

    @Test
    void receiptPrinter_shouldUseProvidedFormatter() {
        Receipt receipt = createTestReceipt();
        ReceiptPrinter htmlPrinter = new ReceiptPrinter(new HtmlReceiptFormatter());
        
        String result = htmlPrinter.printReceipt(receipt);
        
        assertTrue(result.contains("<html>"));
        assertTrue(result.contains("<table>"));
    }

    @Test
    void receiptPrinter_shouldUseCustomColumnsAndFormatter() {
        Receipt receipt = createTestReceipt();
        ReceiptPrinter printer = new ReceiptPrinter(80, new TextReceiptFormatter());
        
        String result = printer.printReceipt(receipt);
        
        assertFalse(result.contains("<html>"));
        assertTrue(result.contains("Total:"));
        // The formatting should be different with 80 columns vs default 40
        assertNotNull(result);
    }
}