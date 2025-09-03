package dojo.supermarket.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiscountStrategyFactoryTest {

    @Test
    void createStrategy_shouldReturnCorrectStrategyForEachOfferType() {
        DiscountStrategy strategy;
        
        strategy = DiscountStrategyFactory.createStrategy(SpecialOfferType.THREE_FOR_TWO);
        assertTrue(strategy instanceof ThreeForTwoDiscountStrategy);
        
        strategy = DiscountStrategyFactory.createStrategy(SpecialOfferType.TEN_PERCENT_DISCOUNT);
        assertTrue(strategy instanceof TenPercentDiscountStrategy);
        
        strategy = DiscountStrategyFactory.createStrategy(SpecialOfferType.TWO_FOR_AMOUNT);
        assertTrue(strategy instanceof TwoForAmountDiscountStrategy);
        
        strategy = DiscountStrategyFactory.createStrategy(SpecialOfferType.FIVE_FOR_AMOUNT);
        assertTrue(strategy instanceof FiveForAmountDiscountStrategy);
    }
}