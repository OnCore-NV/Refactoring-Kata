package dojo.supermarket.model;

public class ThreeForTwoDiscountStrategy implements DiscountStrategy {
    @Override
    public Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument) {
        int quantityAsInt = (int) quantity;
        if (quantityAsInt > 2) {
            int x = 3;
            int numberOfXs = quantityAsInt / x;
            double discountAmount = quantity * unitPrice - ((numberOfXs * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
            return new Discount(product, "3 for 2", -discountAmount);
        }
        return null;
    }
}