package dojo.supermarket;

import dojo.supermarket.model.Receipt;

public interface ReceiptFormatter {
    String formatReceipt(Receipt receipt, int columns);
}