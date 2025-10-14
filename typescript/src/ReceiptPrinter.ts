import {ProductUnit} from "./model/ProductUnit"
import {ReceiptItem} from "./model/ReceiptItem"
import {Receipt} from "./model/Receipt"

export class ReceiptPrinter {
    // Constants for receipt formatting
    private static readonly DEFAULT_COLUMNS = 40;
    private static readonly QUANTITY_THRESHOLD = 1;
    private static readonly PRICE_DECIMAL_PLACES = 2;
    private static readonly WEIGHT_DECIMAL_PLACES = 3;
    private static readonly DISCOUNT_LINE_OFFSET = 3;
    
    // Display strings
    private static readonly UNIT_PRICE_INDENT = "  ";
    private static readonly MULTIPLICATION_SYMBOL = " * ";
    private static readonly TOTAL_LABEL = "Total: ";
    private static readonly WHITESPACE_CHAR = " ";
    private static readonly NEWLINE = "\n";
    private static readonly DISCOUNT_OPEN_PAREN = "(";
    private static readonly DISCOUNT_CLOSE_PAREN = ")";
    private static readonly DISCOUNT_PREFIX = "-";

    public constructor(private readonly columns: number = ReceiptPrinter.DEFAULT_COLUMNS) {
    }

    public printReceipt( receipt: Receipt): string {
        let result = "";
        for (const item of receipt.getItems()) {
            let price = this.format2Decimals(item.totalPrice);
            let quantity = ReceiptPrinter.presentQuantity(item);
            let name = item.product.name;
            let unitPrice = this.format2Decimals(item.price);

            let whitespaceSize = this.columns - name.length - price.length;
            let line = name + ReceiptPrinter.getWhitespace(whitespaceSize) + price + ReceiptPrinter.NEWLINE;

            if (item.quantity != ReceiptPrinter.QUANTITY_THRESHOLD) {
                line += ReceiptPrinter.UNIT_PRICE_INDENT + unitPrice + ReceiptPrinter.MULTIPLICATION_SYMBOL + quantity + ReceiptPrinter.NEWLINE;
            }
            result += line;
        }
        for (const discount of receipt.getDiscounts()) {
            let productPresentation = discount.product.name;
            let pricePresentation = this.format2Decimals(discount.discountAmount);
            let description = discount.description;
            result += description;
            result += ReceiptPrinter.DISCOUNT_OPEN_PAREN;
            result += productPresentation;
            result += ReceiptPrinter.DISCOUNT_CLOSE_PAREN;
            result += ReceiptPrinter.getWhitespace(this.columns - ReceiptPrinter.DISCOUNT_LINE_OFFSET - productPresentation.length - description.length - pricePresentation.length);
            result += ReceiptPrinter.DISCOUNT_PREFIX;
            result += pricePresentation;
            result += ReceiptPrinter.NEWLINE;
        }
        result += ReceiptPrinter.NEWLINE;
        let pricePresentation = this.format2Decimals(receipt.getTotalPrice());
        let total = ReceiptPrinter.TOTAL_LABEL;
        let whitespace = ReceiptPrinter.getWhitespace(this.columns - total.length - pricePresentation.length);
        result += total;
        result += whitespace;
        result += pricePresentation;

        return result;
    }

    private format2Decimals(number: number) {
        return new Intl.NumberFormat('en-UK', {
            minimumFractionDigits: ReceiptPrinter.PRICE_DECIMAL_PLACES,
            maximumFractionDigits: ReceiptPrinter.PRICE_DECIMAL_PLACES
        }).format(number)
    }

    private static presentQuantity( item: ReceiptItem): string  {
        return ProductUnit.Each == item.product.unit
            // TODO make sure this is the simplest way to make something similar to the java version
                ? new Intl.NumberFormat('en-UK', {maximumFractionDigits: 0}).format(item.quantity)
                : new Intl.NumberFormat('en-UK', {minimumFractionDigits: ReceiptPrinter.WEIGHT_DECIMAL_PLACES}).format(item.quantity);
    }

    private static getWhitespace(whitespaceSize: number): string {
        return ReceiptPrinter.WHITESPACE_CHAR.repeat(whitespaceSize);
    }
}
