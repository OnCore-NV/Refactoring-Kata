#include "ShoppingCart.h"

// Constants for magic numbers
namespace {
    constexpr double DEFAULT_ITEM_QUANTITY = 1.0;
    constexpr int TWO_FOR_AMOUNT_QUANTITY = 2;
    constexpr int THREE_FOR_TWO_QUANTITY = 3;
    constexpr int FIVE_FOR_AMOUNT_QUANTITY = 5;
    constexpr double PERCENTAGE_DIVISOR = 100.0;

    // Constants for discount description strings
    const std::string THREE_FOR_TWO_DESCRIPTION = "3 for 2";
    const std::string TWO_FOR_PREFIX = "2 for ";
    const std::string FOR_SEPARATOR = " for ";
    const std::string PERCENT_OFF_SUFFIX = "% off";
}

void addItemQuantity(const Product& product, double quantity);

std::vector<ProductQuantity> ShoppingCart::getItems() const {
    return items;
}

std::map<Product, double> ShoppingCart::getProductQuantities() const {
    return productQuantities;
}

void ShoppingCart::addItem(const Product& product) {
    addItemQuantity(product, DEFAULT_ITEM_QUANTITY);
}

void ShoppingCart::addItemQuantity(const Product& product, double quantity) {
    items.emplace_back(product, quantity);
    if (productQuantities.find(product) != productQuantities.end()) {
        productQuantities[product] += quantity;
    } else {
        productQuantities[product] = quantity;
    }
}

void ShoppingCart::handleOffers(Receipt& receipt, std::map<Product, Offer> offers, SupermarketCatalog* catalog) {
    for (const auto& productQuantity : productQuantities) {
        Product product = productQuantity.first;
        double quantity = productQuantity.second;
        if (offers.find(product) != offers.end()) {
            auto offer = offers[product];
            double unitPrice = catalog->getUnitPrice(product);
            int quantityAsInt = (int) quantity;
            Discount* discount = nullptr;
            int x = 1;

            if (offer.getOfferType() == SpecialOfferType::ThreeForTwo) {
                x = THREE_FOR_TWO_QUANTITY;
            } else if (offer.getOfferType() == SpecialOfferType::TwoForAmount) {
                x = TWO_FOR_AMOUNT_QUANTITY;
                if (quantityAsInt >= TWO_FOR_AMOUNT_QUANTITY) {
                    double total = offer.getArgument() * (quantityAsInt / x) + quantityAsInt % TWO_FOR_AMOUNT_QUANTITY * unitPrice;
                    double discountN = unitPrice * quantity - total;
                    discount = new Discount(TWO_FOR_PREFIX + std::to_string(offer.getArgument()), -discountN, product);
                }
            } if (offer.getOfferType() == SpecialOfferType::FiveForAmount) {
                x = FIVE_FOR_AMOUNT_QUANTITY;
            }
            int numberOfXs = quantityAsInt / x;
            if (offer.getOfferType() == SpecialOfferType::ThreeForTwo && quantityAsInt > TWO_FOR_AMOUNT_QUANTITY) {
                double discountAmount = quantity * unitPrice - ((numberOfXs * TWO_FOR_AMOUNT_QUANTITY * unitPrice) + quantityAsInt % THREE_FOR_TWO_QUANTITY * unitPrice);
                discount = new Discount(THREE_FOR_TWO_DESCRIPTION, -discountAmount, product);
            }
            if (offer.getOfferType() == SpecialOfferType::TenPercentDiscount) {
                discount = new Discount(std::to_string(offer.getArgument()) + PERCENT_OFF_SUFFIX, -quantity * unitPrice * offer.getArgument() / PERCENTAGE_DIVISOR, product);
            }
            if (offer.getOfferType() == SpecialOfferType::FiveForAmount && quantityAsInt >= FIVE_FOR_AMOUNT_QUANTITY) {
                double discountTotal = unitPrice * quantity - (offer.getArgument() * numberOfXs + quantityAsInt % FIVE_FOR_AMOUNT_QUANTITY * unitPrice);
                discount = new Discount(std::to_string(x) + FOR_SEPARATOR + std::to_string(offer.getArgument()), -discountTotal, product);
            }
            if (discount != nullptr)
                receipt.addDiscount(*discount);
        }
    }
}