package supermarket.model

import java.util.ArrayList
import java.util.HashMap

// Constants for magic numbers
private const val DEFAULT_ITEM_QUANTITY = 1.0
private const val TWO_FOR_AMOUNT_QUANTITY = 2
private const val THREE_FOR_TWO_QUANTITY = 3
private const val FIVE_FOR_AMOUNT_QUANTITY = 5
private const val PERCENTAGE_DIVISOR = 100.0

// Constants for discount description strings
private const val THREE_FOR_TWO_DESCRIPTION = "3 for 2"
private const val TWO_FOR_PREFIX = "2 for "
private const val FOR_SEPARATOR = " for "
private const val PERCENT_OFF_SUFFIX = "% off"

class ShoppingCart {

    private val items = ArrayList<ProductQuantity>()
    internal var productQuantities: MutableMap<Product, Double> = HashMap()


    internal fun getItems(): List<ProductQuantity> {
        return ArrayList(items)
    }

    internal fun addItem(product: Product) {
        this.addItemQuantity(product, DEFAULT_ITEM_QUANTITY)
    }

    internal fun productQuantities(): Map<Product, Double> {
        return productQuantities
    }


    fun addItemQuantity(product: Product, quantity: Double) {
        items.add(ProductQuantity(product, quantity))
        if (productQuantities.containsKey(product)) {
            productQuantities[product] = productQuantities[product]!! + quantity
        } else {
            productQuantities[product] = quantity
        }
    }

    internal fun handleOffers(receipt: Receipt, offers: Map<Product, Offer>, catalog: SupermarketCatalog) {
        for (p in productQuantities().keys) {
            val quantity = productQuantities[p]!!
            if (offers.containsKey(p)) {
                val offer = offers[p]!!
                val unitPrice = catalog.getUnitPrice(p)
                val quantityAsInt = quantity.toInt()
                var discount: Discount? = null
                var x = 1
                if (offer.offerType === SpecialOfferType.ThreeForTwo) {
                    x = THREE_FOR_TWO_QUANTITY

                } else if (offer.offerType === SpecialOfferType.TwoForAmount) {
                    x = TWO_FOR_AMOUNT_QUANTITY
                    if (quantityAsInt >= TWO_FOR_AMOUNT_QUANTITY) {
                        val total = offer.argument * (quantityAsInt / x) + quantityAsInt % TWO_FOR_AMOUNT_QUANTITY * unitPrice
                        val discountN = unitPrice * quantity - total
                        discount = Discount(p, TWO_FOR_PREFIX + offer.argument, discountN)
                    }

                }
                if (offer.offerType === SpecialOfferType.FiveForAmount) {
                    x = FIVE_FOR_AMOUNT_QUANTITY
                }
                val numberOfXs = quantityAsInt / x
                if (offer.offerType === SpecialOfferType.ThreeForTwo && quantityAsInt > TWO_FOR_AMOUNT_QUANTITY) {
                    val discountAmount =
                        quantity * unitPrice - (numberOfXs.toDouble() * TWO_FOR_AMOUNT_QUANTITY.toDouble() * unitPrice + quantityAsInt % THREE_FOR_TWO_QUANTITY * unitPrice)
                    discount = Discount(p, THREE_FOR_TWO_DESCRIPTION, discountAmount)
                }
                if (offer.offerType === SpecialOfferType.TenPercentDiscount) {
                    discount =
                        Discount(p, offer.argument.toString() + PERCENT_OFF_SUFFIX, quantity * unitPrice * offer.argument / PERCENTAGE_DIVISOR)
                }
                if (offer.offerType === SpecialOfferType.FiveForAmount && quantityAsInt >= FIVE_FOR_AMOUNT_QUANTITY) {
                    val discountTotal =
                        unitPrice * quantity - (offer.argument * numberOfXs + quantityAsInt % FIVE_FOR_AMOUNT_QUANTITY * unitPrice)
                    discount = Discount(p, x.toString() + FOR_SEPARATOR + offer.argument, discountTotal)
                }
                if (discount != null)
                    receipt.addDiscount(discount)
            }

        }
    }
}
