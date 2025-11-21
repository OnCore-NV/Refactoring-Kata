package supermarket

import (
	"fmt"
	"math"
)

// Constants for magic numbers
const (
	defaultItemQuantity   = 1.0
	twoForAmountQuantity  = 2
	threeForTwoQuantity   = 3
	fiveForAmountQuantity = 5
	percentageDivisor     = 100.0
)

// Constants for discount description strings
const (
	threeForTwoDescription = "3 for 2"
	twoForPrefix           = "2 for %.2f"
	forSeparatorFormat     = "%d for %.2f"
	percentOffSuffixFormat = "%.0f %% off"
)

type ProductQuantity struct {
	product Product
	quantity float64
}

type ShoppingCart struct {
	items []ProductQuantity
	productQuantities map[Product]float64
}

func NewShoppingCart() *ShoppingCart {
	var s ShoppingCart
	s.items = []ProductQuantity{}
	s.productQuantities = make(map[Product]float64)
	return &s
}

func (c *ShoppingCart) addItem(product Product) {
	c.addItemQuantity(product, defaultItemQuantity)
}

func (c *ShoppingCart) addItemQuantity(product Product, amount float64) {
	c.items = append(c.items, ProductQuantity{product: product, quantity: amount})
	currentAmount, ok := c.productQuantities[product]
	if ok {
		c.productQuantities[product] = currentAmount + amount
	} else {
		c.productQuantities[product] = amount
	}
}

func (c *ShoppingCart) handleOffers(receipt *Receipt, offers map[Product]SpecialOffer, catalog Catalog) {
	for p, _ := range c.productQuantities {
		var quantity = c.productQuantities[p]
		if offer, ok := offers[p]; ok  {
			var unitPrice = catalog.unitPrice(p)
			var quantityAsInt = int(math.Round(quantity))
			var discount *Discount = nil
			var x = 1
			if offer.offerType == ThreeForTwo {
				x = threeForTwoQuantity

			} else if offer.offerType == TwoForAmount {
				x = twoForAmountQuantity
				if quantityAsInt >= twoForAmountQuantity {
					var total = offer.argument * float64(quantityAsInt / x) + float64(quantityAsInt % twoForAmountQuantity) * unitPrice
					var discountN = unitPrice * quantity - total;
					discount = &Discount{product: p, description: fmt.Sprintf(twoForPrefix, offer.argument), discountAmount: -discountN}
				}

			}
			if offer.offerType == FiveForAmount {
				x = fiveForAmountQuantity
			}
			var numberOfXs int = quantityAsInt / x;
			if offer.offerType == ThreeForTwo && quantityAsInt > twoForAmountQuantity {
				var discountAmount = quantity * unitPrice - (float64(numberOfXs * twoForAmountQuantity) * unitPrice + float64(quantityAsInt % threeForTwoQuantity) * unitPrice)
				discount = &Discount{product: p, description: threeForTwoDescription, discountAmount: -discountAmount}
			}
			if offer.offerType == TenPercentDiscount {
				discount = &Discount{product: p, description: fmt.Sprintf(percentOffSuffixFormat, offer.argument), discountAmount: -quantity * unitPrice * offer.argument / percentageDivisor}
			}
			if offer.offerType == FiveForAmount && quantityAsInt >= fiveForAmountQuantity {
				var discountTotal = unitPrice * quantity - (offer.argument * float64(numberOfXs) + float64(quantityAsInt % fiveForAmountQuantity) * unitPrice)
				discount = &Discount{product: p, description: fmt.Sprintf(forSeparatorFormat, x, offer.argument), discountAmount: -discountTotal}
			}
			if discount != nil {
				receipt.addDiscount(*discount)
			}

		}

	}
}
