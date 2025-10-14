<?php

declare(strict_types=1);

namespace Supermarket;

use Supermarket\Model\Discount;
use Supermarket\Model\ProductUnit;
use Supermarket\Model\Receipt;
use Supermarket\Model\ReceiptItem;

class ReceiptPrinter
{
    // Constants for receipt formatting
    private const DEFAULT_COLUMNS = 40;
    private const QUANTITY_THRESHOLD = 1.0;
    private const PRICE_DECIMAL_PLACES = 2;
    private const WEIGHT_DECIMAL_PLACES = 3;
    
    // Format strings
    private const PRICE_FORMAT = '%.2F';
    private const WEIGHT_FORMAT = '%.3F';
    private const INTEGER_FORMAT = '%d';
    
    // Display strings
    private const UNIT_PRICE_INDENT = '  ';
    private const MULTIPLICATION_SYMBOL = ' * ';
    private const TOTAL_LABEL = 'Total: ';
    private const WHITESPACE_CHAR = ' ';
    private const NEWLINE = "\n";

    public function __construct(
        private int $columns = self::DEFAULT_COLUMNS
    ) {
    }

    public function printReceipt(Receipt $receipt): string
    {
        $result = '';
        foreach ($receipt->getItems() as $item) {
            $itemPresentation = $this->presentReceiptItem($item);
            $result .= $itemPresentation;
        }

        foreach ($receipt->getDiscounts() as $discount) {
            $discountPresentation = $this->presentDiscount($discount);
            $result .= $discountPresentation;
        }

        $result .= self::NEWLINE;
        $result .= $this->presentTotal($receipt);
        return $result;
    }

    protected function presentReceiptItem(ReceiptItem $item): string
    {
        $price = self::presentPrice($item->getTotalPrice());
        $name = $item->getProduct()->getName();

        $line = $this->formatLineWithWhitespace($name, $price) . self::NEWLINE;

        if ($item->getQuantity() !== self::QUANTITY_THRESHOLD) {
            $line .= self::UNIT_PRICE_INDENT . self::presentPrice($item->getPrice()) . self::MULTIPLICATION_SYMBOL . self::presentQuantity($item) . self::NEWLINE;
        }
        return $line;
    }

    protected function presentDiscount(Discount $discount): string
    {
        $name = "{$discount->getDescription()}({$discount->getProduct()->getName()})";
        $value = self::presentPrice($discount->getDiscountAmount());

        return $this->formatLineWithWhitespace($name, $value) . self::NEWLINE;
    }

    protected function presentTotal(Receipt $receipt): string
    {
        $name = self::TOTAL_LABEL;
        $value = self::presentPrice($receipt->getTotalPrice());
        return $this->formatLineWithWhitespace($name, $value);
    }

    protected function formatLineWithWhitespace(string $name, string $value): string
    {
        $whitespaceSize = $this->columns - strlen($name) - strlen($value);
        return $name . str_repeat(self::WHITESPACE_CHAR, $whitespaceSize) . $value;
    }

    protected static function presentPrice(float $price): string
    {
        return sprintf(self::PRICE_FORMAT, $price);
    }

    private static function presentQuantity(ReceiptItem $item): string
    {
        return $item->getProduct()->getUnit()->equals(ProductUnit::EACH()) ?
            sprintf(self::INTEGER_FORMAT, $item->getQuantity()) :
            sprintf(self::WEIGHT_FORMAT, $item->getQuantity());
    }
}
