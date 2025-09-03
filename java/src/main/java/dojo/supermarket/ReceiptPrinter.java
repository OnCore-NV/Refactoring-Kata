package dojo.supermarket;

import dojo.supermarket.model.*;

public class ReceiptPrinter {

    private final int columns;
    private final ReceiptFormatter formatter;

    public ReceiptPrinter() {
        this(40, new TextReceiptFormatter());
    }

    public ReceiptPrinter(int columns) {
        this(columns, new TextReceiptFormatter());
    }

    public ReceiptPrinter(ReceiptFormatter formatter) {
        this(40, formatter);
    }

    public ReceiptPrinter(int columns, ReceiptFormatter formatter) {
        this.columns = columns;
        this.formatter = formatter;
    }

    public String printReceipt(Receipt receipt) {
        return formatter.formatReceipt(receipt, columns);
    }
}
