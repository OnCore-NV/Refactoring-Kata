package dojo.supermarket.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiscountStrategyTest {

    private final Product testProduct = new Product("test", ProductUnit.EACH);

    @Test
    void threeForTwoStrategy_shouldCalculateCorrectDiscount() {
        DiscountStrategy strategy = new ThreeForTwoDiscountStrategy();
        
        // Test with 3 items - should get discount
        Discount discount = strategy.calculateDiscount(testProduct, 3.0, 1.0, 0.0);
        assertNotNull(discount);
        assertEquals("3 for 2", discount.getDescription());
        assertEquals(-1.0, discount.getDiscountAmount(), 0.01); // Pay for 2, get 1 free
        
        // Test with 6 items - should get discount for 2 sets
        discount = strategy.calculateDiscount(testProduct, 6.0, 1.0, 0.0);
        assertNotNull(discount);
        assertEquals(-2.0, discount.getDiscountAmount(), 0.01); // Pay for 4, get 2 free
        
        // Test with 2 items - should not get discount
        discount = strategy.calculateDiscount(testProduct, 2.0, 1.0, 0.0);
        assertNull(discount);
        
        // Test with 5 items - should get discount for 1 set, pay full for remaining 2
        discount = strategy.calculateDiscount(testProduct, 5.0, 1.0, 0.0);
        assertNotNull(discount);
        assertEquals(-1.0, discount.getDiscountAmount(), 0.01);
    }

    @Test
    void tenPercentDiscountStrategy_shouldCalculateCorrectDiscount() {
        DiscountStrategy strategy = new TenPercentDiscountStrategy();
        
        Discount discount = strategy.calculateDiscount(testProduct, 2.0, 1.0, 10.0);
        assertNotNull(discount);
        assertEquals("10.0% off", discount.getDescription());
        assertEquals(-0.2, discount.getDiscountAmount(), 0.01); // 10% of 2.0
        
        // Test with different percentage
        discount = strategy.calculateDiscount(testProduct, 5.0, 2.0, 20.0);
        assertNotNull(discount);
        assertEquals("20.0% off", discount.getDescription());
        assertEquals(-2.0, discount.getDiscountAmount(), 0.01); // 20% of 10.0
    }

    @Test
    void twoForAmountStrategy_shouldCalculateCorrectDiscount() {
        DiscountStrategy strategy = new TwoForAmountDiscountStrategy();
        
        // Test with 2 items - should get discount
        Discount discount = strategy.calculateDiscount(testProduct, 2.0, 1.0, 1.5);
        assertNotNull(discount);
        assertEquals("2 for 1.5", discount.getDescription());
        assertEquals(-0.5, discount.getDiscountAmount(), 0.01); // Save 0.5 (2.0 - 1.5)
        
        // Test with 4 items - should get discount for 2 sets
        discount = strategy.calculateDiscount(testProduct, 4.0, 1.0, 1.5);
        assertNotNull(discount);
        assertEquals(-1.0, discount.getDiscountAmount(), 0.01); // Save 1.0 (4.0 - 3.0)
        
        // Test with 1 item - should not get discount
        discount = strategy.calculateDiscount(testProduct, 1.0, 1.0, 1.5);
        assertNull(discount);
        
        // Test with 3 items - should get discount for 1 set, pay full for remaining 1
        discount = strategy.calculateDiscount(testProduct, 3.0, 1.0, 1.5);
        assertNotNull(discount);
        assertEquals(-0.5, discount.getDiscountAmount(), 0.01); // Save 0.5 (3.0 - 2.5)
    }

    @Test
    void fiveForAmountStrategy_shouldCalculateCorrectDiscount() {
        DiscountStrategy strategy = new FiveForAmountDiscountStrategy();
        
        // Test with 5 items - should get discount
        Discount discount = strategy.calculateDiscount(testProduct, 5.0, 2.0, 7.0);
        assertNotNull(discount);
        assertEquals("5 for 7.0", discount.getDescription());
        assertEquals(-3.0, discount.getDiscountAmount(), 0.01); // Save 3.0 (10.0 - 7.0)
        
        // Test with 10 items - should get discount for 2 sets
        discount = strategy.calculateDiscount(testProduct, 10.0, 2.0, 7.0);
        assertNotNull(discount);
        assertEquals(-6.0, discount.getDiscountAmount(), 0.01); // Save 6.0 (20.0 - 14.0)
        
        // Test with 4 items - should not get discount
        discount = strategy.calculateDiscount(testProduct, 4.0, 2.0, 7.0);
        assertNull(discount);
        
        // Test with 7 items - should get discount for 1 set, pay full for remaining 2
        discount = strategy.calculateDiscount(testProduct, 7.0, 2.0, 7.0);
        assertNotNull(discount);
        assertEquals(-3.0, discount.getDiscountAmount(), 0.01); // Save 3.0 (14.0 - 11.0)
    }
}