package dojo.supermarket.model;

import dojo.supermarket.ReceiptPrinter;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupermarketTest {

    // Todo: test all kinds of discounts are applied properly

    @Test
    void tenPercentDiscount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);
        Product apples = new Product("apples", ProductUnit.KILO);
        catalog.addProduct(apples, 1.99);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, toothbrush, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.5);
        
        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        assertEquals(4.975, receipt.getTotalPrice(), 0.01);
        assertEquals(Collections.emptyList(), receipt.getDiscounts());
        assertEquals(1, receipt.getItems().size());
        ReceiptItem receiptItem = receipt.getItems().get(0);
        assertEquals(apples, receiptItem.getProduct());
        assertEquals(1.99, receiptItem.getPrice());
        assertEquals(2.5*1.99, receiptItem.getTotalPrice());
        assertEquals(2.5, receiptItem.getQuantity());

    }

    @Test
    void bundleDiscountBasicCase() {
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
        
        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        // Total should be (0.99 + 1.79) - 10% discount = 2.78 - 0.278 = 2.502
        assertEquals(2.502, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        assertEquals(2, receipt.getItems().size());
        
        Discount discount = receipt.getDiscounts().get(0);
        assertEquals(-0.278, discount.getDiscountAmount(), 0.01);
        assertEquals("Bundle discount (10.0% off)", discount.getDescription());
    }

    @Test
    void bundleDiscountOnlyCompleteBundle() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);
        Product toothpaste = new Product("toothpaste", ProductUnit.EACH);
        catalog.addProduct(toothpaste, 1.79);

        Teller teller = new Teller(catalog);
        List<Product> bundleProducts = Arrays.asList(toothbrush, toothpaste);
        teller.addBundleOffer(bundleProducts, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 2.0);  // 2 toothbrushes
        cart.addItemQuantity(toothpaste, 1.0);  // 1 toothpaste
        
        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        // Only 1 complete bundle (limited by toothpaste), so discount on 0.99 + 1.79 = 2.78
        // Total: (2 * 0.99 + 1 * 1.79) - 0.278 = 3.77 - 0.278 = 3.492
        assertEquals(3.492, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        
        Discount discount = receipt.getDiscounts().get(0);
        assertEquals(-0.278, discount.getDiscountAmount(), 0.01);
    }

    @Test
    void bundleDiscountMultipleBundles() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);
        Product toothpaste = new Product("toothpaste", ProductUnit.EACH);
        catalog.addProduct(toothpaste, 1.79);

        Teller teller = new Teller(catalog);
        List<Product> bundleProducts = Arrays.asList(toothbrush, toothpaste);
        teller.addBundleOffer(bundleProducts, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 2.0);
        cart.addItemQuantity(toothpaste, 2.0);
        
        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        // 2 complete bundles, so discount on 2 * (0.99 + 1.79) = 2 * 2.78 = 5.56
        // Discount: 5.56 * 0.1 = 0.556
        // Total: 5.56 - 0.556 = 5.004
        assertEquals(5.004, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        
        Discount discount = receipt.getDiscounts().get(0);
        assertEquals(-0.556, discount.getDiscountAmount(), 0.01);
    }

    @Test
    void noBundleDiscountWhenMissingProduct() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);
        Product toothpaste = new Product("toothpaste", ProductUnit.EACH);
        catalog.addProduct(toothpaste, 1.79);

        Teller teller = new Teller(catalog);
        List<Product> bundleProducts = Arrays.asList(toothbrush, toothpaste);
        teller.addBundleOffer(bundleProducts, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1.0);  // Only toothbrush, no toothpaste
        
        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        // No bundle discount since toothpaste is missing
        assertEquals(0.99, receipt.getTotalPrice(), 0.01);
        assertEquals(Collections.emptyList(), receipt.getDiscounts());
    }

    @Test
    void bundleDiscountWithRegularOffers() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);
        Product toothpaste = new Product("toothpaste", ProductUnit.EACH);
        catalog.addProduct(toothpaste, 1.79);
        Product apples = new Product("apples", ProductUnit.KILO);
        catalog.addProduct(apples, 1.99);

        Teller teller = new Teller(catalog);
        List<Product> bundleProducts = Arrays.asList(toothbrush, toothpaste);
        teller.addBundleOffer(bundleProducts, 10.0);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, apples, 20.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 1.0);
        cart.addItemQuantity(toothpaste, 1.0);
        cart.addItemQuantity(apples, 1.0);
        
        // ACT
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        // ASSERT
        // Bundle discount: (0.99 + 1.79) * 0.1 = 0.278
        // Apples discount: 1.99 * 0.2 = 0.398
        // Total: (0.99 + 1.79 + 1.99) - 0.278 - 0.398 = 4.77 - 0.676 = 4.094
        assertEquals(4.094, receipt.getTotalPrice(), 0.01);
        assertEquals(2, receipt.getDiscounts().size());
    }


}
