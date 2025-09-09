import math

from model_objects import ProductQuantity, SpecialOfferType, Discount


class ShoppingCart:

    def __init__(self):
        self._items = []
        self._product_quantities = {}

    @property
    def items(self):
        return self._items

    def add_item(self, product):
        self.add_item_quantity(product, 1.0)

    @property
    def product_quantities(self):
        return self._product_quantities

    def add_item_quantity(self, product, quantity):
        self._items.append(ProductQuantity(product, quantity))
        if product in self._product_quantities.keys():
            self._product_quantities[product] = self._product_quantities[product] + quantity
        else:
            self._product_quantities[product] = quantity

    def handle_offers(self, receipt, offers, catalog):
        for product in self._product_quantities.keys():
            quantity = self._product_quantities[product]
            if product in offers.keys():
                offer = offers[product]
                unit_price = catalog.unit_price(product)
                quantity_as_int = int(quantity)
                discount = None
                items_per_deal = 1
                if offer.offer_type == SpecialOfferType.THREE_FOR_TWO:
                    items_per_deal = 3

                elif offer.offer_type == SpecialOfferType.TWO_FOR_AMOUNT:
                    items_per_deal = 2
                    if quantity_as_int >= 2:
                        total = offer.argument * (quantity_as_int / items_per_deal) + quantity_as_int % 2 * unit_price
                        discount_amount = unit_price * quantity - total
                        discount = Discount(product, "2 for " + str(offer.argument), -discount_amount)

                if offer.offer_type == SpecialOfferType.FIVE_FOR_AMOUNT:
                    items_per_deal = 5

                number_of_complete_deals = math.floor(quantity_as_int / items_per_deal)
                if offer.offer_type == SpecialOfferType.THREE_FOR_TWO and quantity_as_int > 2:
                    discount_amount = quantity * unit_price - (
                                (number_of_complete_deals * 2 * unit_price) + quantity_as_int % 3 * unit_price)
                    discount = Discount(product, "3 for 2", -discount_amount)

                if offer.offer_type == SpecialOfferType.TEN_PERCENT_DISCOUNT:
                    discount = Discount(product, str(offer.argument) + "% off",
                                        -quantity * unit_price * offer.argument / 100.0)

                if offer.offer_type == SpecialOfferType.FIVE_FOR_AMOUNT and quantity_as_int >= 5:
                    total_discount_amount = unit_price * quantity - (
                                offer.argument * number_of_complete_deals + quantity_as_int % 5 * unit_price)
                    discount = Discount(product, str(items_per_deal) + " for " + str(offer.argument), -total_discount_amount)

                if discount:
                    receipt.add_discount(discount)
