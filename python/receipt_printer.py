from model_objects import ProductUnit

class ReceiptPrinter:
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

    def __init__(self, columns=DEFAULT_COLUMNS):
        self.columns = columns
  
    def print_receipt(self, receipt):
        result = ""
        for item in receipt.items:
            receipt_item = self.print_receipt_item(item)
            result += receipt_item

        for discount in receipt.discounts:
            discount_presentation = self.print_discount(discount)
            result += discount_presentation

        result += self.NEWLINE
        result += self.present_total(receipt)
        return str(result)

    def print_receipt_item(self, item):
        total_price_printed = self.print_price(item.total_price)
        name = item.product.name
        line = self.format_line_with_whitespace(name, total_price_printed)
        if item.quantity != self.QUANTITY_THRESHOLD:
            line += f"{self.UNIT_PRICE_INDENT}{self.print_price(item.price)}{self.MULTIPLICATION_SYMBOL}{self.print_quantity(item)}{self.NEWLINE}"
        return line

    def format_line_with_whitespace(self, name, value):
        line = name
        whitespace_size = self.columns - len(name) - len(value)
        for i in range(whitespace_size):
            line += self.WHITESPACE_CHAR
        line += value
        line += self.NEWLINE
        return line

    def print_price(self, price):
        return self.PRICE_FORMAT % price

    def print_quantity(self, item):
        if ProductUnit.EACH == item.product.unit:
            return str(int(item.quantity))
        else:
            return self.WEIGHT_FORMAT % item.quantity

    def print_discount(self, discount):
        name = f"{discount.description}{self.WHITESPACE_CHAR}{self.DISCOUNT_OPEN_PAREN}{discount.product.name}{self.DISCOUNT_CLOSE_PAREN}"
        value = self.print_price(discount.discount_amount)
        return self.format_line_with_whitespace(name, value)

    def present_total(self, receipt):
        name = self.TOTAL_LABEL
        value = self.print_price(receipt.total_price())
        return self.format_line_with_whitespace(name, value)
