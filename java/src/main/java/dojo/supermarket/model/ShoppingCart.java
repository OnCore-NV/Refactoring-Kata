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
                Discount discount = null;

                switch (offer.offerType) {
                    case THREE_FOR_TWO:
                        discount = calculateThreeForTwoDiscount(p, quantity, unitPrice);
                        break;
                    case TWO_FOR_AMOUNT:
                        discount = calculateTwoForAmountDiscount(p, quantity, unitPrice, offer.argument);
                        break;
                    case TEN_PERCENT_DISCOUNT:
                        discount = calculateTenPercentDiscount(p, quantity, unitPrice, offer.argument);
                        break;
                    case FIVE_FOR_AMOUNT:
                        discount = calculateFiveForAmountDiscount(p, quantity, unitPrice, offer.argument);
                        break;
                }

                if (discount != null)
                    receipt.addDiscount(discount);
            }
        }
    }

    private Discount calculateThreeForTwoDiscount(Product product, double quantity, double unitPrice) {
        int quantityAsInt = (int) quantity;
        if (quantityAsInt <= 2) {
            return null;
        }
        
        int numberOfThrees = quantityAsInt / 3;
        double discountAmount = quantity * unitPrice - (numberOfThrees * 2 * unitPrice + quantityAsInt % 3 * unitPrice);
        return new Discount(product, "3 for 2", -discountAmount);
    }

    private Discount calculateTwoForAmountDiscount(Product product, double quantity, double unitPrice, double amount) {
        int quantityAsInt = (int) quantity;
        if (quantityAsInt < 2) {
            return null;
        }
        
        double total = amount * (quantityAsInt / 2) + quantityAsInt % 2 * unitPrice;
        double discountN = unitPrice * quantity - total;
        return new Discount(product, "2 for " + amount, -discountN);
    }

    private Discount calculateTenPercentDiscount(Product product, double quantity, double unitPrice, double percentage) {
        return new Discount(product, percentage + "% off", -quantity * unitPrice * percentage / 100.0);
    }

    private Discount calculateFiveForAmountDiscount(Product product, double quantity, double unitPrice, double amount) {
        int quantityAsInt = (int) quantity;
        if (quantityAsInt < 5) {
            return null;
        }
        
        int numberOfFives = quantityAsInt / 5;
        double discountTotal = unitPrice * quantity - (amount * numberOfFives + quantityAsInt % 5 * unitPrice);
        return new Discount(product, "5 for " + amount, -discountTotal);
    }
}
