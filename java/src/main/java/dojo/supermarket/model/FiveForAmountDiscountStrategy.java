package dojo.supermarket.model;

public class FiveForAmountDiscountStrategy implements DiscountStrategy {
    @Override
    public Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument) {
        int quantityAsInt = (int) quantity;
        if (quantityAsInt >= 5) {
            int x = 5;
            int numberOfXs = quantityAsInt / x;
            double discountTotal = unitPrice * quantity - (argument * numberOfXs + quantityAsInt % 5 * unitPrice);
            return new Discount(product, x + " for " + argument, -discountTotal);
        }
        return null;
    }
}