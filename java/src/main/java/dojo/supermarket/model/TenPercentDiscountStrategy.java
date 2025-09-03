package dojo.supermarket.model;

public class TenPercentDiscountStrategy implements DiscountStrategy {
    @Override
    public Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument) {
        return new Discount(product, argument + "% off", -quantity * unitPrice * argument / 100.0);
    }
}