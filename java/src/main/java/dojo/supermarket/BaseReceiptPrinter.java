package dojo.supermarket;

import dojo.supermarket.model.*;

import java.util.Locale;

public abstract class BaseReceiptPrinter {

    public abstract String printReceipt(Receipt receipt);
    
    protected abstract String formatReceiptItem(ReceiptItem item);
    
    protected abstract String formatDiscount(Discount discount);
    
    protected abstract String formatTotal(Receipt receipt);

    // Shared data formatting methods - keep these identical across implementations
    protected static String presentPrice(double price) {
        return String.format(Locale.UK, "%.2f", price);
    }

    protected static String presentQuantity(ReceiptItem item) {
        return ProductUnit.EACH.equals(item.getProduct().getUnit())
                ? String.format("%d", (int)item.getQuantity())
                : String.format(Locale.UK, "%.3f", item.getQuantity());
    }
}