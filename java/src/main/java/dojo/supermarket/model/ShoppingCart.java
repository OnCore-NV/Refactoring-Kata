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
        for (Product p: productQuantities().keySet()) {
            double quantity = productQuantities.get(p);
            if (offers.containsKey(p)) {
                Offer offer = offers.get(p);
                double unitPrice = catalog.getUnitPrice(p);
                int quantityAsInt = (int) quantity;
                Discount discount = null;
                int x = 1;
                if (offer.offerType == SpecialOfferType.THREE_FOR_TWO) {
                    x = 3;

                } else if (offer.offerType == SpecialOfferType.TWO_FOR_AMOUNT) {
                    x = 2;
                    if (isEligibleForTwoForAmount(quantityAsInt)) {
                        discount = calculateTwoForAmountDiscount(p, offer, unitPrice, quantity, quantityAsInt, x);
                    }

                } else if (offer.offerType == SpecialOfferType.FIVE_FOR_AMOUNT) {
                    x = 5;
                }
                int numberOfXs = quantityAsInt / x;
                if (offer.offerType == SpecialOfferType.THREE_FOR_TWO && isEligibleForThreeForTwo(quantityAsInt)) {
                    discount = calculateThreeForTwoDiscount(p, unitPrice, quantity, quantityAsInt, numberOfXs);
                }
                if (offer.offerType == SpecialOfferType.TEN_PERCENT_DISCOUNT) {
                    discount = calculateTenPercentDiscount(p, offer, unitPrice, quantity);
                }
                if (offer.offerType == SpecialOfferType.FIVE_FOR_AMOUNT && isEligibleForFiveForAmount(quantityAsInt)) {
                    discount = calculateFiveForAmountDiscount(p, offer, unitPrice, quantity, quantityAsInt, numberOfXs, x);
                }
                if (discount != null)
                    receipt.addDiscount(discount);
            }
        }
    }

    private boolean isEligibleForTwoForAmount(int quantityAsInt) {
        return quantityAsInt >= 2;
    }

    private boolean isEligibleForThreeForTwo(int quantityAsInt) {
        return quantityAsInt > 2;
    }

    private boolean isEligibleForFiveForAmount(int quantityAsInt) {
        return quantityAsInt >= 5;
    }

    private Discount calculateTwoForAmountDiscount(Product p, Offer offer, double unitPrice, double quantity, int quantityAsInt, int x) {
        double total = offer.argument * (quantityAsInt / x) + quantityAsInt % 2 * unitPrice;
        double discountN = unitPrice * quantity - total;
        return new Discount(p, "2 for " + offer.argument, -discountN);
    }

    private Discount calculateThreeForTwoDiscount(Product p, double unitPrice, double quantity, int quantityAsInt, int numberOfXs) {
        double discountAmount = quantity * unitPrice - ((numberOfXs * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
        return new Discount(p, "3 for 2", -discountAmount);
    }

    private Discount calculateTenPercentDiscount(Product p, Offer offer, double unitPrice, double quantity) {
        return new Discount(p, offer.argument + "% off", -quantity * unitPrice * offer.argument / 100.0);
    }

    private Discount calculateFiveForAmountDiscount(Product p, Offer offer, double unitPrice, double quantity, int quantityAsInt, int numberOfXs, int x) {
        double discountTotal = unitPrice * quantity - (offer.argument * numberOfXs + quantityAsInt % 5 * unitPrice);
        return new Discount(p, x + " for " + offer.argument, -discountTotal);
    }
}
