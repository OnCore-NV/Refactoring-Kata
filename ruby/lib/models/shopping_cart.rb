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

class ShoppingCart

  def initialize
    @items = []
    @product_quantities = {}
  end

  def items
    Array.new @items
  end

  def add_item(product)
    add_item_quantity(product, DEFAULT_ITEM_QUANTITY)
    nil
  end

  def product_quantities
    @product_quantities
  end

  def add_item_quantity(product, quantity)
    @items << ProductQuantity.new(product, quantity)
    if @product_quantities.key?(product)
      product_quantities[product] = product_quantities[product] + quantity
    else
      product_quantities[product] = quantity
    end
  end

  def handle_offers(receipt, offers, catalog)
    for p in @product_quantities.keys do
      quantity = @product_quantities[p]
      if offers.key?(p)
        offer = offers[p]
        unit_price = catalog.unit_price(p)
        quantity_as_int = quantity.to_i
        discount = nil
        x = 1
        if offer.offer_type == SpecialOfferType::THREE_FOR_TWO
          x = THREE_FOR_TWO_QUANTITY

        elsif offer.offer_type == SpecialOfferType::TWO_FOR_AMOUNT
          x = TWO_FOR_AMOUNT_QUANTITY
          if quantity_as_int >= TWO_FOR_AMOUNT_QUANTITY
            total = offer.argument * (quantity_as_int / x) + quantity_as_int % TWO_FOR_AMOUNT_QUANTITY * unit_price
            discount_n = unit_price * quantity - total
            discount = Discount.new(p, TWO_FOR_PREFIX + offer.argument.to_s, discount_n)
          end

        end
        if offer.offer_type == SpecialOfferType:: FIVE_FOR_AMOUNT
          x = FIVE_FOR_AMOUNT_QUANTITY
        end
        number_of_x = quantity_as_int / x
        if offer.offer_type == SpecialOfferType::THREE_FOR_TWO && quantity_as_int > TWO_FOR_AMOUNT_QUANTITY
          discount_amount = quantity * unit_price - ((number_of_x * TWO_FOR_AMOUNT_QUANTITY * unit_price) + quantity_as_int % THREE_FOR_TWO_QUANTITY * unit_price)
          discount = Discount.new(p, THREE_FOR_TWO_DESCRIPTION, discount_amount)
        end
        if offer.offer_type == SpecialOfferType::TEN_PERCENT_DISCOUNT
          discount = Discount.new(p, offer.argument.to_s + PERCENT_OFF_SUFFIX, quantity * unit_price * offer.argument / PERCENTAGE_DIVISOR)
        end
        if offer.offer_type == SpecialOfferType::FIVE_FOR_AMOUNT && quantity_as_int >= FIVE_FOR_AMOUNT_QUANTITY
          discount_total = unit_price * quantity - (offer.argument * number_of_x + quantity_as_int % FIVE_FOR_AMOUNT_QUANTITY * unit_price)
          discount = Discount.new(p, x.to_s + FOR_SEPARATOR + offer.argument.to_s, discount_total)
        end

        receipt.add_discount(discount) if discount
      end
    end
  end

end
