<?php

declare(strict_types=1);

namespace Supermarket\Model;

use Ds\Map;

class ShoppingCart
{
    // Constants for magic numbers
    private const DEFAULT_ITEM_QUANTITY = 1.0;
    private const TWO_FOR_AMOUNT_QUANTITY = 2;
    private const THREE_FOR_TWO_QUANTITY = 3;
    private const FIVE_FOR_AMOUNT_QUANTITY = 5;
    private const PERCENTAGE_DIVISOR = 100.0;

    // Constants for discount description strings
    private const THREE_FOR_TWO_DESCRIPTION = '3 for 2';
    private const PERCENT_OFF_SUFFIX = '% off';

    /**
     * @var ProductQuantity[]
     */
    private array $items = [];

    /**
     * @var Map<Product, float>
     */
    private Map $productQuantities;

    public function __construct()
    {
        $this->productQuantities = new Map();
    }

    public function addItem(Product $product): void
    {
        $this->addItemQuantity($product, self::DEFAULT_ITEM_QUANTITY);
    }

    /**
     * @return ProductQuantity[]
     */
    public function getItems(): array
    {
        return $this->items;
    }

    public function addItemQuantity(Product $product, float $quantity): void
    {
        $this->items[] = new ProductQuantity($product, $quantity);
        if ($this->productQuantities->hasKey($product)) {
            $newAmount = $this->productQuantities[$product] + $quantity;
            $this->productQuantities[$product] = $newAmount;
        } else {
            $this->productQuantities[$product] = $quantity;
        }
    }

    /**
     * @param Map<Product, Offer> $offers
     */
    public function handleOffers(Receipt $receipt, Map $offers, SupermarketCatalog $catalog): void
    {
        /**
         * @var Product $p
         * @var float $quantity
         */
        foreach ($this->productQuantities as $p => $quantity) {
            $quantityAsInt = (int) $quantity;
            if ($offers->hasKey($p)) {
                /** @var Offer $offer */
                $offer = $offers[$p];
                $unitPrice = $catalog->getUnitPrice($p);
                $discount = null;
                $x = 1;
                if ($offer->getOfferType()->equals(SpecialOfferType::THREE_FOR_TWO())) {
                    $x = self::THREE_FOR_TWO_QUANTITY;
                } elseif ($offer->getOfferType()->equals(SpecialOfferType::TWO_FOR_AMOUNT())) {
                    $x = self::TWO_FOR_AMOUNT_QUANTITY;
                    if ($quantityAsInt >= self::TWO_FOR_AMOUNT_QUANTITY) {
                        $total = $offer->getArgument() * intdiv($quantityAsInt, $x) + $quantityAsInt % self::TWO_FOR_AMOUNT_QUANTITY * $unitPrice;
                        $discountN = $unitPrice * $quantity - $total;
                        $discount = new Discount($p, "2 for {$offer->getArgument()}", -1 * $discountN);
                    }
                }

                if ($offer->getOfferType()->equals(SpecialOfferType::FIVE_FOR_AMOUNT())) {
                    $x = self::FIVE_FOR_AMOUNT_QUANTITY;
                }
                $numberOfXs = intdiv($quantityAsInt, $x);
                if ($offer->getOfferType()->equals(SpecialOfferType::THREE_FOR_TWO()) && $quantityAsInt > self::TWO_FOR_AMOUNT_QUANTITY) {
                    $discountAmount = $quantity * $unitPrice - ($numberOfXs * self::TWO_FOR_AMOUNT_QUANTITY * $unitPrice + $quantityAsInt % self::THREE_FOR_TWO_QUANTITY * $unitPrice);
                    $discount = new Discount($p, self::THREE_FOR_TWO_DESCRIPTION, -$discountAmount);
                }

                if ($offer->getOfferType()->equals(SpecialOfferType::TEN_PERCENT_DISCOUNT())) {
                    $discount = new Discount(
                        $p,
                        "{$offer->getArgument()}" . self::PERCENT_OFF_SUFFIX,
                        -$quantity * $unitPrice * $offer->getArgument() / self::PERCENTAGE_DIVISOR
                    );
                }
                if ($offer->getOfferType()->equals(SpecialOfferType::FIVE_FOR_AMOUNT()) && $quantityAsInt >= self::FIVE_FOR_AMOUNT_QUANTITY) {
                    $discountTotal = $unitPrice * $quantity - ($offer->getArgument() * $numberOfXs + $quantityAsInt % self::FIVE_FOR_AMOUNT_QUANTITY * $unitPrice);
                    $discount = new Discount($p, "${x} for {$offer->getArgument()}", -$discountTotal);
                }

                if ($discount !== null) {
                    $receipt->addDiscount($discount);
                }
            }
        }
    }
}
