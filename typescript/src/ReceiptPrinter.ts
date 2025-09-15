import {ProductUnit} from "./model/ProductUnit"
import {ReceiptItem} from "./model/ReceiptItem"
import {Receipt} from "./model/Receipt"

export class ReceiptPrinter {
    private static readonly DEFAULT_COLUMN_WIDTH = 40;
    private static readonly PRICE_DECIMAL_PLACES = 2;
    private static readonly WHOLE_QUANTITY_DECIMAL_PLACES = 0;
    private static readonly FRACTIONAL_QUANTITY_DECIMAL_PLACES = 3;
    private static readonly DEFAULT_ITEM_QUANTITY = 1;
    private static readonly DISCOUNT_SPACING_OFFSET = 3;

    public constructor(private readonly columns: number = ReceiptPrinter.DEFAULT_COLUMN_WIDTH) {
    }

    public printReceipt( receipt: Receipt): string {
        let result = "";
        for (const item of receipt.getItems()) {
            let price = this.format2Decimals(item.totalPrice);
            let quantity = ReceiptPrinter.presentQuantity(item);
            let name = item.product.name;
            let unitPrice = this.format2Decimals(item.price);

            let whitespaceSize = this.columns - name.length - price.length;
            let line = name + ReceiptPrinter.getWhitespace(whitespaceSize) + price + "\n";

            if (item.quantity != ReceiptPrinter.DEFAULT_ITEM_QUANTITY) {
                line += "  " + unitPrice + " * " + quantity + "\n";
            }
            result += line;
        }
        for (const discount of receipt.getDiscounts()) {
            let productPresentation = discount.product.name;
            let pricePresentation = this.format2Decimals(discount.discountAmount);
            let description = discount.description;
            result += description;
            result += "(";
            result += productPresentation;
            result += ")";
            result += ReceiptPrinter.getWhitespace(this.columns - ReceiptPrinter.DISCOUNT_SPACING_OFFSET - productPresentation.length - description.length - pricePresentation.length);
            result += "-";
            result += pricePresentation;
            result += "\n";
        }
        result += "\n";
        let pricePresentation = this.format2Decimals(receipt.getTotalPrice());
        let total = "Total: ";
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
                ? new Intl.NumberFormat('en-UK', {maximumFractionDigits: ReceiptPrinter.WHOLE_QUANTITY_DECIMAL_PLACES}).format(item.quantity)
                : new Intl.NumberFormat('en-UK', {minimumFractionDigits: ReceiptPrinter.FRACTIONAL_QUANTITY_DECIMAL_PLACES}).format(item.quantity);
    }

    private static getWhitespace(whitespaceSize: number): string {
        return " ".repeat(whitespaceSize);
    }
}
