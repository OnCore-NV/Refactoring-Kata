package supermarket

import supermarket.model.ProductUnit
import supermarket.model.Receipt
import supermarket.model.ReceiptItem
import java.util.*

class ReceiptPrinter @JvmOverloads constructor(private val columns: Int = DEFAULT_COLUMNS) {
    companion object {
        // Constants for receipt formatting
        private const val DEFAULT_COLUMNS = 40
        private const val QUANTITY_THRESHOLD = 1.0
        private const val PRICE_DECIMAL_PLACES = 2
        private const val WEIGHT_DECIMAL_PLACES = 3
        private const val DISCOUNT_LINE_OFFSET = 3
        
        // Display strings
        private const val UNIT_PRICE_INDENT = "  "
        private const val MULTIPLICATION_SYMBOL = " * "
        private const val TOTAL_LABEL = "Total: "
        private const val WHITESPACE_CHAR = " "
        private const val NEWLINE = "\n"
        private const val DISCOUNT_OPEN_PAREN = "("
        private const val DISCOUNT_CLOSE_PAREN = ")"
        private const val DISCOUNT_PREFIX = "-"
    }

    fun printReceipt(receipt: Receipt): String {
        val result = StringBuilder()
        for (item in receipt.getItems()) {
            val price = String.format(Locale.UK, "%.$PRICE_DECIMAL_PLACES" + "f", item.totalPrice)
            val quantity = presentQuantity(item)
            val name = item.product.name
            val unitPrice = String.format(Locale.UK, "%.$PRICE_DECIMAL_PLACES" + "f", item.price)

            val whitespaceSize = this.columns - name.length - price.length
            var line = name + getWhitespace(whitespaceSize) + price + NEWLINE

            if (item.quantity != QUANTITY_THRESHOLD) {
                line += "$UNIT_PRICE_INDENT$unitPrice$MULTIPLICATION_SYMBOL$quantity$NEWLINE"
            }
            result.append(line)
        }
        for (discount in receipt.getDiscounts()) {
            val productPresentation = discount.product.name
            val pricePresentation = String.format(Locale.UK, "%.$PRICE_DECIMAL_PLACES" + "f", discount.discountAmount)
            val description = discount.description
            result.append(description)
            result.append(DISCOUNT_OPEN_PAREN)
            result.append(productPresentation)
            result.append(DISCOUNT_CLOSE_PAREN)
            result.append(getWhitespace(this.columns - DISCOUNT_LINE_OFFSET - productPresentation.length - description.length - pricePresentation.length))
            result.append(DISCOUNT_PREFIX)
            result.append(pricePresentation)
            result.append(NEWLINE)
        }
        result.append(NEWLINE)
        val pricePresentation = String.format(Locale.UK, "%.$PRICE_DECIMAL_PLACES" + "f", receipt.totalPrice as Double)
        val total = TOTAL_LABEL
        val whitespace = getWhitespace(this.columns - total.length - pricePresentation.length)
        result.append(total).append(whitespace).append(pricePresentation)
        return result.toString()
    }

    private fun presentQuantity(item: ReceiptItem): String {
        return if (ProductUnit.Each.equals(item.product.unit))
            String.format("%d", item.quantity.toInt())
        else
            String.format(Locale.UK, "%.$WEIGHT_DECIMAL_PLACES" + "f", item.quantity)
    }

    private fun getWhitespace(whitespaceSize: Int): String {
        val whitespace = StringBuilder()
        for (i in 0 until whitespaceSize) {
            whitespace.append(WHITESPACE_CHAR)
        }
        return whitespace.toString()
    }
}
