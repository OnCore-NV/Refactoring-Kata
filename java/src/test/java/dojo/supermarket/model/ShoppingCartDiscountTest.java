package dojo.supermarket.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartDiscountTest {

    @Test
    void handleOffers_shouldApplyThreeForTwoDiscount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 3.0);
        
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(1.98, receipt.getTotalPrice(), 0.01); // Pay for 2, get 1 free
        assertEquals(1, receipt.getDiscounts().size());
        assertEquals("3 for 2", receipt.getDiscounts().get(0).getDescription());
    }

    @Test
    void handleOffers_shouldApplyTenPercentDiscount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product apples = new Product("apples", ProductUnit.KILO);
        catalog.addProduct(apples, 1.99);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TEN_PERCENT_DISCOUNT, apples, 10.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(apples, 2.0);
        
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(3.582, receipt.getTotalPrice(), 0.01); // 2 * 1.99 * 0.9
        assertEquals(1, receipt.getDiscounts().size());
        assertEquals("10.0% off", receipt.getDiscounts().get(0).getDescription());
    }

    @Test
    void handleOffers_shouldApplyTwoForAmountDiscount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product cherryTomatoes = new Product("cherry tomatoes", ProductUnit.EACH);
        catalog.addProduct(cherryTomatoes, 0.69);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.TWO_FOR_AMOUNT, cherryTomatoes, 0.99);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(cherryTomatoes, 2.0);
        
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(0.99, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        assertEquals("2 for 0.99", receipt.getDiscounts().get(0).getDescription());
    }

    @Test
    void handleOffers_shouldApplyFiveForAmountDiscount() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothpaste = new Product("toothpaste", ProductUnit.EACH);
        catalog.addProduct(toothpaste, 1.79);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.FIVE_FOR_AMOUNT, toothpaste, 7.49);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothpaste, 5.0);
        
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(7.49, receipt.getTotalPrice(), 0.01);
        assertEquals(1, receipt.getDiscounts().size());
        assertEquals("5 for 7.49", receipt.getDiscounts().get(0).getDescription());
    }

    @Test
    void handleOffers_shouldNotApplyDiscountWhenQuantityTooLow() {
        SupermarketCatalog catalog = new FakeCatalog();
        Product toothbrush = new Product("toothbrush", ProductUnit.EACH);
        catalog.addProduct(toothbrush, 0.99);

        Teller teller = new Teller(catalog);
        teller.addSpecialOffer(SpecialOfferType.THREE_FOR_TWO, toothbrush, 0.0);

        ShoppingCart cart = new ShoppingCart();
        cart.addItemQuantity(toothbrush, 2.0);
        
        Receipt receipt = teller.checksOutArticlesFrom(cart);

        assertEquals(1.98, receipt.getTotalPrice(), 0.01); // No discount
        assertEquals(0, receipt.getDiscounts().size());
    }
}