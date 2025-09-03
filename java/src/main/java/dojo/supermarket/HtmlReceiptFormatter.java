package dojo.supermarket;

import dojo.supermarket.model.*;

import java.util.Locale;

public class HtmlReceiptFormatter implements ReceiptFormatter {

    @Override
    public String formatReceipt(Receipt receipt, int columns) {
        StringBuilder result = new StringBuilder();
        
        result.append("<html>\n<body>\n");
        result.append("<h1>Receipt</h1>\n");
        result.append("<table>\n");
        
        for (ReceiptItem item : receipt.getItems()) {
            String receiptItem = presentReceiptItem(item);
            result.append(receiptItem);
        }
        
        for (Discount discount : receipt.getDiscounts()) {
            String discountPresentation = presentDiscount(discount);
            result.append(discountPresentation);
        }

        result.append(presentTotal(receipt));
        result.append("</table>\n</body>\n</html>");
        return result.toString();
    }

    private String presentReceiptItem(ReceiptItem item) {
        StringBuilder result = new StringBuilder();
        result.append("  <tr>\n");
        result.append("    <td>").append(escapeHtml(item.getProduct().getName())).append("</td>\n");
        result.append("    <td>").append(presentPrice(item.getTotalPrice())).append("</td>\n");
        result.append("  </tr>\n");

        if (item.getQuantity() != 1) {
            result.append("  <tr>\n");
            result.append("    <td colspan=\"2\">").append(presentPrice(item.getPrice()))
                  .append(" * ").append(presentQuantity(item)).append("</td>\n");
            result.append("  </tr>\n");
        }
        return result.toString();
    }

    private String presentDiscount(Discount discount) {
        String name = discount.getDescription() + "(" + discount.getProduct().getName() + ")";
        String value = presentPrice(discount.getDiscountAmount());

        return "  <tr class=\"discount\">\n" +
               "    <td>" + escapeHtml(name) + "</td>\n" +
               "    <td>" + value + "</td>\n" +
               "  </tr>\n";
    }

    private String presentTotal(Receipt receipt) {
        return "  <tr class=\"total\">\n" +
               "    <td><strong>Total:</strong></td>\n" +
               "    <td><strong>" + presentPrice(receipt.getTotalPrice()) + "</strong></td>\n" +
               "  </tr>\n";
    }

    private static String presentPrice(double price) {
        return String.format(Locale.UK, "%.2f", price);
    }

    private static String presentQuantity(ReceiptItem item) {
        return ProductUnit.EACH.equals(item.getProduct().getUnit())
                ? String.format("%d", (int)item.getQuantity())
                : String.format(Locale.UK, "%.3f", item.getQuantity());
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}