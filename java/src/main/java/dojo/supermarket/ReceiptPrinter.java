package dojo.supermarket;

import dojo.supermarket.model.*;

import java.util.Locale;

public class ReceiptPrinter extends BaseReceiptPrinter {

    private final int columns;

    public ReceiptPrinter() {
        this(40);
    }

    public ReceiptPrinter(int columns) {
        this.columns = columns;
    }

    @Override
    public String printReceipt(Receipt receipt) {
        StringBuilder result = new StringBuilder();
        for (ReceiptItem item : receipt.getItems()) {
            String receiptItem = formatReceiptItem(item);
            result.append(receiptItem);
        }
        for (Discount discount : receipt.getDiscounts()) {
            String discountPresentation = formatDiscount(discount);
            result.append(discountPresentation);
        }

        result.append("\n");
        result.append(formatTotal(receipt));
        return result.toString();
    }

    @Override
    protected String formatReceiptItem(ReceiptItem item) {
        return presentReceiptItem(item);
    }

    @Override
    protected String formatDiscount(Discount discount) {
        return presentDiscount(discount);
    }

    @Override
    protected String formatTotal(Receipt receipt) {
        return presentTotal(receipt);
    }

    private String presentReceiptItem(ReceiptItem item) {
        String totalPricePresentation = presentPrice(item.getTotalPrice());
        String name = item.getProduct().getName();

        String line = formatLineWithWhitespace(name, totalPricePresentation);

        if (item.getQuantity() != 1) {
            line += "  " + presentPrice(item.getPrice()) + " * " + presentQuantity(item) + "\n";
        }
        return line;
    }

    private String presentDiscount(Discount discount) {
        String name = discount.getDescription() + "(" + discount.getProduct().getName() + ")";
        String value = presentPrice(discount.getDiscountAmount());

        return formatLineWithWhitespace(name, value);
    }

    private String presentTotal(Receipt receipt) {
        String name = "Total: ";
        String value = presentPrice(receipt.getTotalPrice());
        return formatLineWithWhitespace(name, value);
    }

    private String formatLineWithWhitespace(String name, String value) {
        StringBuilder line = new StringBuilder();
        line.append(name);
        int whitespaceSize = this.columns - name.length() - value.length();
        for (int i = 0; i < whitespaceSize; i++) {
            line.append(" ");
        }
        line.append(value);
        line.append('\n');
        return line.toString();
    }

    protected static String presentPrice(double price) {
        return BaseReceiptPrinter.presentPrice(price);
    }

    protected static String presentQuantity(ReceiptItem item) {
        return BaseReceiptPrinter.presentQuantity(item);
    }
}
