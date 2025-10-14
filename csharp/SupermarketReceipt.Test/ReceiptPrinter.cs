using System.Globalization;
using System.Text;

namespace SupermarketReceipt
{
    public class ReceiptPrinter
    {
        // Constants for receipt formatting
        private const int DefaultColumns = 40;
        private const int QuantityThreshold = 1;
        private const int PriceDecimalPlaces = 2;
        private const int WeightDecimalPlaces = 3;
        
        // Display strings
        private const string UnitPriceIndent = "  ";
        private const string MultiplicationSymbol = " * ";
        private const string TotalLabel = "Total: ";
        private const string WhitespaceChar = " ";
        private const string Newline = "\n";
        
        private static readonly CultureInfo Culture = CultureInfo.CreateSpecificCulture("en-GB");

        private readonly int _columns;


        public ReceiptPrinter(int columns)
        {
            _columns = columns;
        }

        public ReceiptPrinter() : this(DefaultColumns)
        {
        }

        public string PrintReceipt(Receipt receipt)
        {
            var result = new StringBuilder();
            foreach (var item in receipt.GetItems())
            {
                string receiptItem = PrintReceiptItem(item);
                result.Append(receiptItem);
                
            }

            foreach (var discount in receipt.GetDiscounts())
            {
                string discountPresentation = PrintDiscount(discount);
                result.Append(discountPresentation);
            }

            {
                result.Append(Newline);
                result.Append(PrintTotal(receipt));
            }
            return result.ToString();
        }

        private string PrintTotal(Receipt receipt)
        {
            string name = TotalLabel;
            string value = PrintPrice(receipt.GetTotalPrice());
            return FormatLineWithWhitespace(name, value);
        }

        private string PrintDiscount(Discount discount)
        {
            string name = discount.Description + "(" + discount.Product.Name + ")";
            string value = PrintPrice(discount.DiscountAmount);

            return FormatLineWithWhitespace(name, value);
        }

        private string PrintReceiptItem(ReceiptItem item)
        {
            string totalPrice = PrintPrice(item.TotalPrice);
            string name = item.Product.Name;
            string line = FormatLineWithWhitespace(name, totalPrice);
            if (item.Quantity != QuantityThreshold)
            {
                line += UnitPriceIndent + PrintPrice(item.Price) + MultiplicationSymbol + PrintQuantity(item) + Newline;
            }

            return line;
        }
        

        private string FormatLineWithWhitespace(string name, string value)
        {
            var line = new StringBuilder();
            line.Append(name);
            int whitespaceSize = this._columns - name.Length - value.Length;
            for (int i = 0; i < whitespaceSize; i++) {
                line.Append(WhitespaceChar);
            }
            line.Append(value);
            line.Append(Newline);
            return line.ToString();
        }

        private string PrintPrice(double price)
        {
            return price.ToString("N" + PriceDecimalPlaces, Culture);
        }

        private static string PrintQuantity(ReceiptItem item)
        {
            return ProductUnit.Each == item.Product.Unit
                ? ((int) item.Quantity).ToString()
                : item.Quantity.ToString("N" + WeightDecimalPlaces, Culture);
        }
        
    }
}