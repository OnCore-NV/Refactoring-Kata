// Constants for magic numbers
private let defaultItemQuantity = 1.0
private let twoForAmountQuantity = 2
private let threeForTwoQuantity = 3
private let fiveForAmountQuantity = 5
private let percentageDivisor = 100.0

// Constants for discount description strings
private let threeForTwoDescription = "3 for 2"
private let twoForPrefix = "2 for "
private let forSeparator = " for "
private let percentOffSuffix = "% off"

public class ShoppingCart {

    public var items = [ProductQuantity]()
    public var productQuantities = [Product: Double]()

    func addItem(product: Product) {
        self.addItemQuantity(product: product, quantity: defaultItemQuantity)
    }

    public func addItemQuantity(product: Product , quantity: Double) {
        items.append(ProductQuantity(product: product, quantity:quantity))
        if productQuantities[product] != nil {
            productQuantities[product] = productQuantities[product]! + quantity
        } else {
            productQuantities[product] = quantity
        }
    }

    func handleOffers(receipt: Receipt, offers: [Product: Offer], catalog: SupermarketCatalog ) {
        for p in productQuantities.keys {
            var quantity = productQuantities[p]
            if offers[p] != nil {
                var offer = offers[p]
                var unitPrice = catalog.getUnitPrice(product: p)
                var quantityAsInt = Int(quantity!)
                var discount: Discount? = nil
                var x = 1
                if offer?.offerType == SpecialOfferType.ThreeForTwo {
                    x = threeForTwoQuantity

                } else if offer?.offerType == SpecialOfferType.TwoForAmount {
                    x = twoForAmountQuantity
                    if (quantityAsInt >= twoForAmountQuantity) {
                        
                        var intDivision = quantityAsInt / x
                        
                        var pricePerUnit = (offer!.argument * Double(intDivision))
                        
                        var theTotal = Double(quantityAsInt % twoForAmountQuantity) * unitPrice
                        
                        var total = pricePerUnit + theTotal
                        var discountN = unitPrice * quantity! - total
                        discount =  Discount(description:  twoForPrefix + "\(offer!.argument)", discountAmount: discountN, product: p)
                    }

                } else if offer?.offerType == SpecialOfferType.FiveForAmount {
                    x = fiveForAmountQuantity
                }
                var numberOfXs = quantityAsInt / x
                if offer?.offerType == SpecialOfferType.ThreeForTwo && quantityAsInt > twoForAmountQuantity {
                    var left = Double(numberOfXs * twoForAmountQuantity) * unitPrice
                    var right = Double(quantityAsInt % threeForTwoQuantity) * unitPrice
                    var lastPart = left + right
                    var discountAmount = ((quantity ?? 1) * unitPrice) - lastPart
                    discount =  Discount(description: threeForTwoDescription, discountAmount: discountAmount, product: p)
                }
                if offer?.offerType == SpecialOfferType.TenPercentDiscount {
                    discount =  Discount(description: "\(offer!.argument)" + percentOffSuffix, discountAmount: (quantity ?? 1) * unitPrice * (offer?.argument ?? 1) / percentageDivisor, product: p)
                }
                if offer?.offerType == SpecialOfferType.FiveForAmount && quantityAsInt >= fiveForAmountQuantity {
                    var left = (unitPrice * (quantity ?? 1))
                    var right = ((offer?.argument ?? 1) * Double(numberOfXs)) + (Double(quantityAsInt % fiveForAmountQuantity) * unitPrice)
                    var discountTotal = left - right
                    discount =  Discount(description: "\(x)" + forSeparator + "\(offer!.argument)", discountAmount: discountTotal,  product: p)
                }
                if discount != nil {
                    receipt.addDiscount(discount: discount!)
                }
            }

        }
    }
}
