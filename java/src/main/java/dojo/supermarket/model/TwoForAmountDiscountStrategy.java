package dojo.supermarket.model;

public class TwoForAmountDiscountStrategy implements DiscountStrategy {
    @Override
    public Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument) {
        int quantityAsInt = (int) quantity;
        if (quantityAsInt >= 2) {
            int x = 2;
            double total = argument * (quantityAsInt / x) + quantityAsInt % 2 * unitPrice;
            double discountN = unitPrice * quantity - total;
            return new Discount(product, "2 for " + argument, -discountN);
        }
        return null;
    }
}