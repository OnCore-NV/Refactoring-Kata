public class ReceiptPrinter {
    // Constants for receipt formatting
    private static let defaultColumns = 40
    private static let quantityThreshold = 1.0
    private static let priceDecimalPlaces = 2
    private static let weightDecimalPlaces = 3
    private static let discountLineOffset = 3
    
    // Display strings
    private static let unitPriceIndent = "  "
    private static let multiplicationSymbol = " * "
    private static let totalLabel = "Total: "
    private static let whitespaceChar = " "
    private static let newline = "\n"
    private static let discountOpenParen = "("
    private static let discountCloseParen = ")"
    private static let discountPrefix = "-"
    
    private var columns: Int = defaultColumns

    public init(columns: Int) {
        self.columns = columns
    }

    public func printReceipt(receipt: Receipt) -> String {
        var result = ""
        for item in receipt.items {
            var price = String(format: "%.\(ReceiptPrinter.priceDecimalPlaces)f", item.totalPrice)
            var quantity = ReceiptPrinter.presentQuantity(item: item)
            var name = item.product.name
            var unitPrice = String(format: "%.\(ReceiptPrinter.priceDecimalPlaces)f", item.price)

            var whitespaceSize = self.columns - name.count - price.count
            var line = name + ReceiptPrinter.getWhitespace(whitespaceSize: whitespaceSize) + price + ReceiptPrinter.newline

            if (item.quantity != ReceiptPrinter.quantityThreshold) {
                line += ReceiptPrinter.unitPriceIndent + unitPrice + ReceiptPrinter.multiplicationSymbol + quantity + ReceiptPrinter.newline
            }
            result.append(line)
        }
        for discount in receipt.discounts {
            var productPresentation = discount.product.name
            var pricePresentation = String(format: "%.\(ReceiptPrinter.priceDecimalPlaces)f", discount.discountAmount)
            var description = discount.description
            result.append(description)
            result.append(ReceiptPrinter.discountOpenParen)
            result.append(productPresentation)
            result.append(ReceiptPrinter.discountCloseParen)
            result.append(ReceiptPrinter.getWhitespace(whitespaceSize: self.columns - ReceiptPrinter.discountLineOffset - productPresentation.count - description.count - pricePresentation.count))
            result.append(ReceiptPrinter.discountPrefix)
            result.append(pricePresentation)
            result.append(ReceiptPrinter.newline)
        }
        result.append(ReceiptPrinter.newline)
        var pricePresentation = String(format: "%.\(ReceiptPrinter.priceDecimalPlaces)f", Double(receipt.getTotalPrice()))
        var total = ReceiptPrinter.totalLabel
        var whitespace = ReceiptPrinter.getWhitespace(whitespaceSize: self.columns - total.count - pricePresentation.count)
        result.append(total)
        result.append(whitespace)
        result.append(pricePresentation)
        return result
    }

    private static func presentQuantity(item: ReceiptItem ) -> String {
        return ProductUnit.Each == item.product.unit
            ? String(format: "%d", Int(item.quantity))
            : String(format: "%.\(weightDecimalPlaces)f", item.quantity)
    }

    private static func getWhitespace(whitespaceSize: Int) -> String {
        var whitespace = ""
        for i in 0..<whitespaceSize {
            whitespace.append(whitespaceChar)
        }
        return whitespace
    }
}
