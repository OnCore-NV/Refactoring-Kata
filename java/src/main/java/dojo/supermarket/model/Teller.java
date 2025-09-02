package dojo.supermarket.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class Teller {

    private final SupermarketCatalog catalog;
    private final Map<Product, Offer> offers = new HashMap<>();
    private final List<BundleOffer> bundleOffers = new ArrayList<>();

    public Teller(SupermarketCatalog catalog) {
        this.catalog = catalog;
    }

    public void addSpecialOffer(SpecialOfferType offerType, Product product, double argument) {
        offers.put(product, new Offer(offerType, product, argument));
    }

    public void addBundleOffer(List<Product> products, double discountPercent) {
        bundleOffers.add(new BundleOffer(products, discountPercent));
    }

    public Receipt checksOutArticlesFrom(ShoppingCart theCart) {
        Receipt receipt = new Receipt();
        List<ProductQuantity> productQuantities = theCart.getItems();
        for (ProductQuantity pq: productQuantities) {
            Product p = pq.getProduct();
            double quantity = pq.getQuantity();
            double unitPrice = catalog.getUnitPrice(p);
            double price = quantity * unitPrice;
            receipt.addProduct(p, quantity, unitPrice, price);
        }
        theCart.handleOffers(receipt, offers, catalog);
        theCart.handleBundleOffers(receipt, bundleOffers, catalog);

        return receipt;
    }
}
