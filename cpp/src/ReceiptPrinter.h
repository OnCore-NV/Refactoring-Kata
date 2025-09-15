#ifndef CPP_RECEIPTPRINTER_H
#define CPP_RECEIPTPRINTER_H


#include "Receipt.h"

#include <iomanip>
#include <sstream>

class ReceiptPrinter
{
private:
    // Receipt formatting constants
    static constexpr int DEFAULT_COLUMN_WIDTH = 40;
    static constexpr int PRICE_DECIMAL_PLACES = 2;
    static constexpr int WHOLE_QUANTITY_DECIMAL_PLACES = 0;
    static constexpr int FRACTIONAL_QUANTITY_DECIMAL_PLACES = 3;
    static constexpr int DEFAULT_ITEM_QUANTITY = 1;

public:
    ReceiptPrinter() : ReceiptPrinter(DEFAULT_COLUMN_WIDTH)
    {
    }

    ReceiptPrinter(int columns) : columns(columns)
    {}

    std::string printReceipt(const Receipt &receipt)
    {
        std::string result;
        for (const auto &item : receipt.getItems())
        {
            result.append(presentReceiptItem(item));
        }
        for (const auto &discount : receipt.getDiscounts())
        {
            result.append(presentDiscount(discount));
        }
        result.append("\n");
        result.append(presentTotal(receipt));
        return result;
    }

    std::string presentReceiptItem(const ReceiptItem &item) const
    {
        std::string price = getFormattedNumberAsString(item.getTotalPrice(), PRICE_DECIMAL_PLACES);
        std::string name = item.getProduct().getName();

        std::string line = formatLineWithWhitespace(name, price);

        if (item.getQuantity() != DEFAULT_ITEM_QUANTITY)
        {
            line += "  " + getFormattedNumberAsString(item.getPrice(), PRICE_DECIMAL_PLACES) + " * " + presentQuantity(item) + "\n";
        }
        return line;
    }

    std::string presentDiscount(const Discount &discount) const
    {
        std::string name = discount.getDescription() + "(" + discount.getProduct().getName() + ")";
        std::string pricePresentation = getFormattedNumberAsString(discount.getDiscountAmount(), PRICE_DECIMAL_PLACES);
        return formatLineWithWhitespace(name, pricePresentation);
    }

    std::string presentTotal(const Receipt &receipt) const
    {
        std::string total = "Total: ";
        std::string pricePresentation = presentPrice(receipt.getTotalPrice());
        return formatLineWithWhitespace(total, pricePresentation);
    }

    std::string formatLineWithWhitespace(const std::string &name, const std::string &value) const
    {
        int whitespaceSize = columns - name.length() - value.length();
        std::string whitespace;
        for (int i = 0; i < whitespaceSize; i++)
        {
            whitespace.append(" ");
        }
        return name + whitespace + value + "\n";
    }

    std::string presentPrice(double price) const
    { return getFormattedNumberAsString(price, PRICE_DECIMAL_PLACES); }

    static std::string presentQuantity(const ReceiptItem &item)
    {
        return ProductUnit::Each == item.getProduct().getUnit()
               ? getFormattedNumberAsString(item.getQuantity(), WHOLE_QUANTITY_DECIMAL_PLACES)
               : getFormattedNumberAsString(item.getQuantity(), FRACTIONAL_QUANTITY_DECIMAL_PLACES);
    }

private:

    static std::string getFormattedNumberAsString(double number, int precision)
    {
        std::stringstream stream;
        stream << std::fixed << std::setprecision(precision) << number;
        return stream.str();
    }

    const int columns;


};


#endif //CPP_RECEIPTPRINTER_H
