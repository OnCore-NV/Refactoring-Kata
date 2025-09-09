package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

    private final List<ProductQuantity> items = new ArrayList<>();
    private final Map<Product, Double> productQuantities = new HashMap<>();

    List<ProductQuantity> getItems() {
        return Collections.unmodifiableList(items);
    }

    void addItem(Product product) {
        addItemQuantity(product, 1.0);
    }

    Map<Product, Double> productQuantities() {
        return Collections.unmodifiableMap(productQuantities);
    }

    public void addItemQuantity(Product product, double quantity) {
        items.add(new ProductQuantity(product, quantity));
        if (productQuantities.containsKey(product)) {
            productQuantities.put(product, productQuantities.get(product) + quantity);
        } else {
            productQuantities.put(product, quantity);
        }
    }

    void handleOffers(Receipt receipt, Map<Product, Offer> offers, SupermarketCatalog catalog) {
        for (Product product: productQuantities().keySet()) {
            double quantity = productQuantities.get(product);
            if (offers.containsKey(product)) {
                Offer offer = offers.get(product);
                double unitPrice = catalog.getUnitPrice(product);
                int quantityAsInt = (int) quantity;
                Discount discount = null;
                int itemsPerDeal = 1;
                if (offer.offerType == SpecialOfferType.THREE_FOR_TWO) {
                    itemsPerDeal = 3;

                } else if (offer.offerType == SpecialOfferType.TWO_FOR_AMOUNT) {
                    itemsPerDeal = 2;
                    if (quantityAsInt >= 2) {
                        double total = offer.argument * (quantityAsInt / itemsPerDeal) + quantityAsInt % 2 * unitPrice;
                        double discountAmount = unitPrice * quantity - total;
                        discount = new Discount(product, "2 for " + offer.argument, -discountAmount);
                    }

                } if (offer.offerType == SpecialOfferType.FIVE_FOR_AMOUNT) {
                    itemsPerDeal = 5;
                }
                int numberOfCompleteDeals = quantityAsInt / itemsPerDeal;
                if (offer.offerType == SpecialOfferType.THREE_FOR_TWO && quantityAsInt > 2) {
                    double discountAmount = quantity * unitPrice - ((numberOfCompleteDeals * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
                    discount = new Discount(product, "3 for 2", -discountAmount);
                }
                if (offer.offerType == SpecialOfferType.TEN_PERCENT_DISCOUNT) {
                    discount = new Discount(product, offer.argument + "% off", -quantity * unitPrice * offer.argument / 100.0);
                }
                if (offer.offerType == SpecialOfferType.FIVE_FOR_AMOUNT && quantityAsInt >= 5) {
                    double totalDiscountAmount = unitPrice * quantity - (offer.argument * numberOfCompleteDeals + quantityAsInt % 5 * unitPrice);
                    discount = new Discount(product, itemsPerDeal + " for " + offer.argument, -totalDiscountAmount);
                }
                if (discount != null)
                    receipt.addDiscount(discount);
            }
        }
    }
}
