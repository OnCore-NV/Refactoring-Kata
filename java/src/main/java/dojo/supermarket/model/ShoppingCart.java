package dojo.supermarket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {

    // Constants for magic numbers
    private static final double DEFAULT_ITEM_QUANTITY = 1.0;
    private static final int TWO_FOR_AMOUNT_QUANTITY = 2;
    private static final int THREE_FOR_TWO_QUANTITY = 3;
    private static final int FIVE_FOR_AMOUNT_QUANTITY = 5;
    private static final double PERCENTAGE_DIVISOR = 100.0;

    // Constants for discount description strings
    private static final String THREE_FOR_TWO_DESCRIPTION = "3 for 2";
    private static final String TWO_FOR_PREFIX = "2 for ";
    private static final String FOR_SEPARATOR = " for ";
    private static final String PERCENT_OFF_SUFFIX = "% off";

    private final List<ProductQuantity> items = new ArrayList<>();
    private final Map<Product, Double> productQuantities = new HashMap<>();

    List<ProductQuantity> getItems() {
        return Collections.unmodifiableList(items);
    }

    void addItem(Product product) {
        addItemQuantity(product, DEFAULT_ITEM_QUANTITY);
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
                    x = THREE_FOR_TWO_QUANTITY;

                } else if (offer.offerType == SpecialOfferType.TWO_FOR_AMOUNT) {
                    x = TWO_FOR_AMOUNT_QUANTITY;
                    if (quantityAsInt >= TWO_FOR_AMOUNT_QUANTITY) {
                        double total = offer.argument * (quantityAsInt / x) + quantityAsInt % TWO_FOR_AMOUNT_QUANTITY * unitPrice;
                        double discountN = unitPrice * quantity - total;
                        discount = new Discount(p, TWO_FOR_PREFIX + offer.argument, -discountN);
                    }

                } if (offer.offerType == SpecialOfferType.FIVE_FOR_AMOUNT) {
                    x = FIVE_FOR_AMOUNT_QUANTITY;
                }
                int numberOfXs = quantityAsInt / x;
                if (offer.offerType == SpecialOfferType.THREE_FOR_TWO && quantityAsInt > TWO_FOR_AMOUNT_QUANTITY) {
                    double discountAmount = quantity * unitPrice - ((numberOfXs * TWO_FOR_AMOUNT_QUANTITY * unitPrice) + quantityAsInt % THREE_FOR_TWO_QUANTITY * unitPrice);
                    discount = new Discount(p, THREE_FOR_TWO_DESCRIPTION, -discountAmount);
                }
                if (offer.offerType == SpecialOfferType.TEN_PERCENT_DISCOUNT) {
                    discount = new Discount(p, offer.argument + PERCENT_OFF_SUFFIX, -quantity * unitPrice * offer.argument / PERCENTAGE_DIVISOR);
                }
                if (offer.offerType == SpecialOfferType.FIVE_FOR_AMOUNT && quantityAsInt >= FIVE_FOR_AMOUNT_QUANTITY) {
                    double discountTotal = unitPrice * quantity - (offer.argument * numberOfXs + quantityAsInt % FIVE_FOR_AMOUNT_QUANTITY * unitPrice);
                    discount = new Discount(p, x + FOR_SEPARATOR + offer.argument, -discountTotal);
                }
                if (discount != null)
                    receipt.addDiscount(discount);
            }
        }
    }
}
