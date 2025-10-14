package dojo.supermarket;

import dojo.supermarket.model.*;

import java.util.Locale;

public class ReceiptPrinter {
    // Constants for receipt formatting
    private static final int DEFAULT_COLUMNS = 40;
    private static final int QUANTITY_THRESHOLD = 1;
    private static final int PRICE_DECIMAL_PLACES = 2;
    private static final int WEIGHT_DECIMAL_PLACES = 3;
    
    // Display strings
    private static final String UNIT_PRICE_INDENT = "  ";
    private static final String MULTIPLICATION_SYMBOL = " * ";
    private static final String TOTAL_LABEL = "Total: ";
    private static final String WHITESPACE_CHAR = " ";
    private static final String NEWLINE = "\n";

    private final int columns;

    public ReceiptPrinter() {
        this(DEFAULT_COLUMNS);
    }

    public ReceiptPrinter(int columns) {
        this.columns = columns;
    }

    public String printReceipt(Receipt receipt) {
        StringBuilder result = new StringBuilder();
        for (ReceiptItem item : receipt.getItems()) {
            String receiptItem = presentReceiptItem(item);
            result.append(receiptItem);
        }
        for (Discount discount : receipt.getDiscounts()) {
            String discountPresentation = presentDiscount(discount);
            result.append(discountPresentation);
        }

        result.append(NEWLINE);
        result.append(presentTotal(receipt));
        return result.toString();
    }

    private String presentReceiptItem(ReceiptItem item) {
        String totalPricePresentation = presentPrice(item.getTotalPrice());
        String name = item.getProduct().getName();

        String line = formatLineWithWhitespace(name, totalPricePresentation);

        if (item.getQuantity() != QUANTITY_THRESHOLD) {
            line += UNIT_PRICE_INDENT + presentPrice(item.getPrice()) + MULTIPLICATION_SYMBOL + presentQuantity(item) + NEWLINE;
        }
        return line;
    }

    private String presentDiscount(Discount discount) {
        String name = discount.getDescription() + "(" + discount.getProduct().getName() + ")";
        String value = presentPrice(discount.getDiscountAmount());

        return formatLineWithWhitespace(name, value);
    }

    private String presentTotal(Receipt receipt) {
        String name = TOTAL_LABEL;
        String value = presentPrice(receipt.getTotalPrice());
        return formatLineWithWhitespace(name, value);
    }

    private String formatLineWithWhitespace(String name, String value) {
        StringBuilder line = new StringBuilder();
        line.append(name);
        int whitespaceSize = this.columns - name.length() - value.length();
        for (int i = 0; i < whitespaceSize; i++) {
            line.append(WHITESPACE_CHAR);
        }
        line.append(value);
        line.append(NEWLINE);
        return line.toString();
    }

    private static String presentPrice(double price) {
        return String.format(Locale.UK, "%." + PRICE_DECIMAL_PLACES + "f", price);
    }

    private static String presentQuantity(ReceiptItem item) {
        return ProductUnit.EACH.equals(item.getProduct().getUnit())
                ? String.format("%d", (int)item.getQuantity())
                : String.format(Locale.UK, "%." + WEIGHT_DECIMAL_PLACES + "f", item.getQuantity());
    }
}
