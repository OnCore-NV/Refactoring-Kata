package dojo.supermarket;

import dojo.supermarket.model.*;

import java.util.Locale;

public class TextReceiptFormatter implements ReceiptFormatter {

    @Override
    public String formatReceipt(Receipt receipt, int columns) {
        StringBuilder result = new StringBuilder();
        
        for (ReceiptItem item : receipt.getItems()) {
            String receiptItem = presentReceiptItem(item, columns);
            result.append(receiptItem);
        }
        
        for (Discount discount : receipt.getDiscounts()) {
            String discountPresentation = presentDiscount(discount, columns);
            result.append(discountPresentation);
        }

        result.append("\n");
        result.append(presentTotal(receipt, columns));
        return result.toString();
    }

    private String presentReceiptItem(ReceiptItem item, int columns) {
        String totalPricePresentation = presentPrice(item.getTotalPrice());
        String name = item.getProduct().getName();

        String line = formatLineWithWhitespace(name, totalPricePresentation, columns);

        if (item.getQuantity() != 1) {
            line += "  " + presentPrice(item.getPrice()) + " * " + presentQuantity(item) + "\n";
        }
        return line;
    }

    private String presentDiscount(Discount discount, int columns) {
        String name = discount.getDescription() + "(" + discount.getProduct().getName() + ")";
        String value = presentPrice(discount.getDiscountAmount());

        return formatLineWithWhitespace(name, value, columns);
    }

    private String presentTotal(Receipt receipt, int columns) {
        String name = "Total: ";
        String value = presentPrice(receipt.getTotalPrice());
        return formatLineWithWhitespace(name, value, columns);
    }

    private String formatLineWithWhitespace(String name, String value, int columns) {
        StringBuilder line = new StringBuilder();
        line.append(name);
        int whitespaceSize = columns - name.length() - value.length();
        for (int i = 0; i < whitespaceSize; i++) {
            line.append(" ");
        }
        line.append(value);
        line.append('\n');
        return line.toString();
    }

    private static String presentPrice(double price) {
        return String.format(Locale.UK, "%.2f", price);
    }

    private static String presentQuantity(ReceiptItem item) {
        return ProductUnit.EACH.equals(item.getProduct().getUnit())
                ? String.format("%d", (int)item.getQuantity())
                : String.format(Locale.UK, "%.3f", item.getQuantity());
    }
}