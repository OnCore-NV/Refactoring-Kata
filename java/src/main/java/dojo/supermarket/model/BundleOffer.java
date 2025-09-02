package dojo.supermarket.model;

import java.util.List;
import java.util.Collections;

public class BundleOffer {
    private final List<Product> products;
    private final double discountPercent;

    public BundleOffer(List<Product> products, double discountPercent) {
        this.products = Collections.unmodifiableList(products);
        this.discountPercent = discountPercent;
    }

    public List<Product> getProducts() {
        return products;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }
}