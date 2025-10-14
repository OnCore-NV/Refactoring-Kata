class ReceiptPrinter
  # Constants for receipt formatting
  DEFAULT_COLUMNS = 40
  QUANTITY_THRESHOLD = 1
  PRICE_DECIMAL_PLACES = 2
  WEIGHT_DECIMAL_PLACES = 3
  DISCOUNT_LINE_OFFSET = 3
  
  # Format strings
  PRICE_FORMAT = "%.2f"
  WEIGHT_FORMAT = "%.3f"
  
  # Display strings
  UNIT_PRICE_INDENT = "  "
  MULTIPLICATION_SYMBOL = " * "
  TOTAL_LABEL = "Total: "
  WHITESPACE_CHAR = " "
  NEWLINE = "\n"
  DISCOUNT_OPEN_PAREN = "("
  DISCOUNT_CLOSE_PAREN = ")"
  DISCOUNT_PREFIX = "-"

  def initialize(columns = DEFAULT_COLUMNS)
    @columns = columns
  end

  def print_receipt(receipt)
    result = ""
    for item in receipt.items do
      price = PRICE_FORMAT % item.total_price
      quantity = self.class.present_quantity(item)
      name = item.product.name
      unit_price = PRICE_FORMAT % item.price

      whitespace_size = @columns - name.size - price.size
      line = name + self.class.whitespace(whitespace_size) + price + NEWLINE

      if item.quantity != QUANTITY_THRESHOLD
        line += UNIT_PRICE_INDENT + unit_price + MULTIPLICATION_SYMBOL + quantity + NEWLINE
      end

      result.concat(line);
    end
    for discount in receipt.discounts do
      product_presentation = discount.product.name
      price_presentation = PRICE_FORMAT % discount.discount_amount
      description = discount.description
      result.concat(description)
      result.concat(DISCOUNT_OPEN_PAREN)
      result.concat(product_presentation)
      result.concat(DISCOUNT_CLOSE_PAREN)
      result.concat(self.class.whitespace(@columns - DISCOUNT_LINE_OFFSET - product_presentation.size - description.size - price_presentation.size))
      result.concat(DISCOUNT_PREFIX);
      result.concat(price_presentation);
      result.concat(NEWLINE);
    end
    result.concat(NEWLINE)
    price_presentation = PRICE_FORMAT % receipt.total_price.to_f
    total = TOTAL_LABEL
    whitespace = self.class.whitespace(@columns - total.size - price_presentation.size)
    result.concat(total, whitespace, price_presentation)
    return result.to_s
  end

  def self.present_quantity(item)
    return ProductUnit::EACH == item.product.unit ? '%d' % item.quantity.to_i : WEIGHT_FORMAT % item.quantity
  end

  def self.whitespace(whitespace_size)
    whitespace = ''
    whitespace_size.times do
      whitespace.concat(WHITESPACE_CHAR)
    end
    return whitespace
  end

end
