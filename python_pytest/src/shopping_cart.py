import math

from model_objects import ProductQuantity, SpecialOfferType, Discount

# Constants for magic numbers
DEFAULT_ITEM_QUANTITY = 1.0
TWO_FOR_AMOUNT_QUANTITY = 2
THREE_FOR_TWO_QUANTITY = 3
FIVE_FOR_AMOUNT_QUANTITY = 5
PERCENTAGE_DIVISOR = 100.0

# Constants for discount description strings
THREE_FOR_TWO_DESCRIPTION = "3 for 2"
TWO_FOR_PREFIX = "2 for "
FOR_SEPARATOR = " for "
PERCENT_OFF_SUFFIX = "% off"


class ShoppingCart:

    def __init__(self):
        self._items = []
        self._product_quantities = {}

    @property
    def items(self):
        return self._items

    def add_item(self, product):
        self.add_item_quantity(product, DEFAULT_ITEM_QUANTITY)

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
        for p in self._product_quantities.keys():
            quantity = self._product_quantities[p]
            if p in offers.keys():
                offer = offers[p]
                unit_price = catalog.unit_price(p)
                quantity_as_int = int(quantity)
                discount = None
                x = 1
                if offer.offer_type == SpecialOfferType.THREE_FOR_TWO:
                    x = THREE_FOR_TWO_QUANTITY

                elif offer.offer_type == SpecialOfferType.TWO_FOR_AMOUNT:
                    x = TWO_FOR_AMOUNT_QUANTITY
                    if quantity_as_int >= TWO_FOR_AMOUNT_QUANTITY:
                        total = offer.argument * (quantity_as_int / x) + quantity_as_int % TWO_FOR_AMOUNT_QUANTITY * unit_price
                        discount_n = unit_price * quantity - total
                        discount = Discount(p, TWO_FOR_PREFIX + str(offer.argument), -discount_n)

                if offer.offer_type == SpecialOfferType.FIVE_FOR_AMOUNT:
                    x = FIVE_FOR_AMOUNT_QUANTITY

                number_of_x = math.floor(quantity_as_int / x)
                if offer.offer_type == SpecialOfferType.THREE_FOR_TWO and quantity_as_int > TWO_FOR_AMOUNT_QUANTITY:
                    discount_amount = quantity * unit_price - (
                                (number_of_x * TWO_FOR_AMOUNT_QUANTITY * unit_price) + quantity_as_int % THREE_FOR_TWO_QUANTITY * unit_price)
                    discount = Discount(p, THREE_FOR_TWO_DESCRIPTION, -discount_amount)

                if offer.offer_type == SpecialOfferType.TEN_PERCENT_DISCOUNT:
                    discount = Discount(p, str(offer.argument) + PERCENT_OFF_SUFFIX,
                                        -quantity * unit_price * offer.argument / PERCENTAGE_DIVISOR)

                if offer.offer_type == SpecialOfferType.FIVE_FOR_AMOUNT and quantity_as_int >= FIVE_FOR_AMOUNT_QUANTITY:
                    discount_total = unit_price * quantity - (
                                offer.argument * number_of_x + quantity_as_int % FIVE_FOR_AMOUNT_QUANTITY * unit_price)
                    discount = Discount(p, str(x) + FOR_SEPARATOR + str(offer.argument), -discount_total)

                if discount:
                    receipt.add_discount(discount)
