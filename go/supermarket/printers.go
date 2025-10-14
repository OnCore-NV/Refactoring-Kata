package supermarket

import (
	"fmt"
	"golang.org/x/text/language"
	"golang.org/x/text/message"
	"strings"
)

// Constants for receipt formatting
const (
	defaultColumns        = 40
	quantityThreshold     = 1
	priceDecimalPlaces    = 2
	weightDecimalPlaces   = 3
	unitPriceIndent       = "  "
	multiplicationSymbol  = " * "
	totalLabel            = "Total: "
	whitespaceChar        = " "
	newline               = "\n"
	priceFormat           = "%.2f"
	weightFormat          = "%.3f"
)

type ReceiptPrinter struct {
	columns int
	lp *message.Printer
}

func NewReceiptPrinter() *ReceiptPrinter {
	var p ReceiptPrinter
	p.columns = defaultColumns
	p.lp = message.NewPrinter(language.BritishEnglish)
	return &p
}

func (p ReceiptPrinter) printReceipt(receipt *Receipt) string {
	var result string
	for _, item := range receipt.sortedItems() {
		var receiptItem = p.presentReceiptItem(item)
		result += receiptItem
	}
	for _, discount := range receipt.sortedDiscounts() {
		result += p.presentDiscount(discount)
	}
	result += newline
	result += p.presentTotal(receipt)

	return result
}

func (p ReceiptPrinter) presentReceiptItem(item ReceiptItem) string {
	var totalPricePresentation string = p.presentPrice(item.totalPrice)
	var line = p.formatLineWithWhitespace(item.product.name, totalPricePresentation)
	if item.quantity != quantityThreshold {
		line += fmt.Sprintf("%s%s%s%s%s", unitPriceIndent, p.presentPrice(item.price), multiplicationSymbol, p.presentQuantity(item), newline)
	}
	return line
}

func (p ReceiptPrinter) formatLineWithWhitespace(name string, value string) string {
	var result strings.Builder
	fmt.Fprint(&result, name)
	var whitespaceSize = p.columns - len(name) - len(value)
	for i := 1; i <= whitespaceSize; i++ {
		fmt.Fprint(&result, whitespaceChar)
	}
	fmt.Fprintf(&result, "%s%s", value, newline)
	return result.String()
}

func (p ReceiptPrinter) presentPrice(price float64) string {
	return p.lp.Sprintf(priceFormat, price)
}

func (p ReceiptPrinter) presentQuantity(item ReceiptItem) string {
	var result string
	if Each == item.product.unit {
		result = fmt.Sprintf("%d", int(item.quantity))
	} else {
		result = p.lp.Sprintf(weightFormat, item.quantity)
	}
	return result
}

func (p ReceiptPrinter) presentTotal(receipt *Receipt) string {
	var name = totalLabel
	var value = p.presentPrice(receipt.totalPrice())
	return p.formatLineWithWhitespace(name, value)
}

func (p ReceiptPrinter) presentDiscount(discount Discount) string {
	var name = fmt.Sprintf("%s (%s)", discount.description, discount.product.name)
	var value = p.presentPrice(discount.discountAmount)
	return p.formatLineWithWhitespace(name, value)
}

