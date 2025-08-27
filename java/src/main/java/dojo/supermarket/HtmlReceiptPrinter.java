package dojo.supermarket;

import dojo.supermarket.model.*;

public class HtmlReceiptPrinter extends BaseReceiptPrinter {

    @Override
    public String printReceipt(Receipt receipt) {
        StringBuilder result = new StringBuilder();
        
        result.append("<html>\n");
        result.append("<body>\n");
        result.append("<h1>Receipt</h1>\n");
        result.append("<table>\n");
        
        for (ReceiptItem item : receipt.getItems()) {
            result.append(formatReceiptItem(item));
        }
        
        for (Discount discount : receipt.getDiscounts()) {
            result.append(formatDiscount(discount));
        }
        
        result.append(formatTotal(receipt));
        result.append("</table>\n");
        result.append("</body>\n");
        result.append("</html>\n");
        
        return result.toString();
    }

    @Override
    protected String formatReceiptItem(ReceiptItem item) {
        StringBuilder result = new StringBuilder();
        String totalPricePresentation = presentPrice(item.getTotalPrice());
        String name = item.getProduct().getName();

        result.append("<tr>");
        result.append("<td>").append(name).append("</td>");
        result.append("<td>").append(totalPricePresentation).append("</td>");
        result.append("</tr>\n");

        if (item.getQuantity() != 1) {
            result.append("<tr>");
            result.append("<td colspan='2'>");
            result.append("  ").append(presentPrice(item.getPrice()));
            result.append(" * ").append(presentQuantity(item));
            result.append("</td>");
            result.append("</tr>\n");
        }
        
        return result.toString();
    }

    @Override
    protected String formatDiscount(Discount discount) {
        String name = discount.getDescription() + "(" + discount.getProduct().getName() + ")";
        String value = presentPrice(discount.getDiscountAmount());

        StringBuilder result = new StringBuilder();
        result.append("<tr>");
        result.append("<td>").append(name).append("</td>");
        result.append("<td>").append(value).append("</td>");
        result.append("</tr>\n");
        
        return result.toString();
    }

    @Override
    protected String formatTotal(Receipt receipt) {
        String name = "Total: ";
        String value = presentPrice(receipt.getTotalPrice());
        
        StringBuilder result = new StringBuilder();
        result.append("<tr>");
        result.append("<td><strong>").append(name).append("</strong></td>");
        result.append("<td><strong>").append(value).append("</strong></td>");
        result.append("</tr>\n");
        
        return result.toString();
    }
}