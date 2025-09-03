package dojo.supermarket.model;

public interface DiscountStrategy {
    Discount calculateDiscount(Product product, double quantity, double unitPrice, double argument);
}