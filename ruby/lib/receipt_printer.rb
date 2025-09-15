class ReceiptPrinter
  # Receipt formatting constants
  DEFAULT_COLUMN_WIDTH = 40
  PRICE_DECIMAL_PLACES = 2
  FRACTIONAL_QUANTITY_DECIMAL_PLACES = 3
  DEFAULT_ITEM_QUANTITY = 1
  DISCOUNT_SPACING_OFFSET = 3

  def initialize(columns = DEFAULT_COLUMN_WIDTH)
    @columns = columns
  end

  def print_receipt(receipt)
    result = ""
    for item in receipt.items do
      price = "%.#{PRICE_DECIMAL_PLACES}f" % item.total_price
      quantity = self.class.present_quantity(item)
      name = item.product.name
      unit_price = "%.#{PRICE_DECIMAL_PLACES}f" % item.price

      whitespace_size = @columns - name.size - price.size
      line = name + self.class.whitespace(whitespace_size) + price + "\n"

      if item.quantity != DEFAULT_ITEM_QUANTITY
        line += "  " + unit_price + " * " + quantity + "\n"
      end

      result.concat(line);
    end
    for discount in receipt.discounts do
      product_presentation = discount.product.name
      price_presentation = "%.#{PRICE_DECIMAL_PLACES}f" % discount.discount_amount
      description = discount.description
      result.concat(description)
      result.concat("(")
      result.concat(product_presentation)
      result.concat(")")
      result.concat(self.class.whitespace(@columns - DISCOUNT_SPACING_OFFSET - product_presentation.size - description.size - price_presentation.size))
      result.concat("-");
      result.concat(price_presentation);
      result.concat("\n");
    end
    result.concat("\n")
    price_presentation = "%.#{PRICE_DECIMAL_PLACES}f" % receipt.total_price.to_f
    total = "Total: "
    whitespace = self.class.whitespace(@columns - total.size - price_presentation.size)
    result.concat(total, whitespace, price_presentation)
    return result.to_s
  end

  def self.present_quantity(item)
    return ProductUnit::EACH == item.product.unit ? '%x' % item.quantity.to_i : "%.#{FRACTIONAL_QUANTITY_DECIMAL_PLACES}f" % item.quantity
  end

  def self.whitespace(whitespace_size)
    whitespace = ''
    whitespace_size.times do
      whitespace.concat(' ')
    end
    return whitespace
  end

end
