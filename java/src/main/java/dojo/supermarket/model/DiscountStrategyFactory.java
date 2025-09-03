package dojo.supermarket.model;

public class DiscountStrategyFactory {
    public static DiscountStrategy createStrategy(SpecialOfferType offerType) {
        switch (offerType) {
            case THREE_FOR_TWO:
                return new ThreeForTwoDiscountStrategy();
            case TEN_PERCENT_DISCOUNT:
                return new TenPercentDiscountStrategy();
            case TWO_FOR_AMOUNT:
                return new TwoForAmountDiscountStrategy();
            case FIVE_FOR_AMOUNT:
                return new FiveForAmountDiscountStrategy();
            default:
                throw new IllegalArgumentException("Unknown offer type: " + offerType);
        }
    }
}