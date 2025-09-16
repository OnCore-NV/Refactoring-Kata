<?php

declare(strict_types=1);

namespace Supermarket\Model;

use Ds\Map;

class ShoppingCart
{
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
        $this->addItemQuantity($product, 1.0);
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
            if ($offers->hasKey($p)) {
                /** @var Offer $offer */
                $offer = $offers[$p];
                $unitPrice = $catalog->getUnitPrice($p);
                $discount = null;

                if ($offer->getOfferType()->equals(SpecialOfferType::THREE_FOR_TWO())) {
                    $discount = $this->calculateThreeForTwoDiscount($p, $quantity, $unitPrice);
                } elseif ($offer->getOfferType()->equals(SpecialOfferType::TWO_FOR_AMOUNT())) {
                    $discount = $this->calculateTwoForAmountDiscount($p, $quantity, $unitPrice, $offer->getArgument());
                } elseif ($offer->getOfferType()->equals(SpecialOfferType::TEN_PERCENT_DISCOUNT())) {
                    $discount = $this->calculateTenPercentDiscount($p, $quantity, $unitPrice, $offer->getArgument());
                } elseif ($offer->getOfferType()->equals(SpecialOfferType::FIVE_FOR_AMOUNT())) {
                    $discount = $this->calculateFiveForAmountDiscount($p, $quantity, $unitPrice, $offer->getArgument());
                }

                if ($discount !== null) {
                    $receipt->addDiscount($discount);
                }
            }
        }
    }

    private function calculateThreeForTwoDiscount(Product $product, float $quantity, float $unitPrice): ?Discount
    {
        $quantityAsInt = (int) $quantity;
        if ($quantityAsInt <= 2) {
            return null;
        }
        
        $numberOfThrees = intdiv($quantityAsInt, 3);
        $discountAmount = $quantity * $unitPrice - ($numberOfThrees * 2 * $unitPrice + $quantityAsInt % 3 * $unitPrice);
        return new Discount($product, '3 for 2', -$discountAmount);
    }

    private function calculateTwoForAmountDiscount(Product $product, float $quantity, float $unitPrice, float $amount): ?Discount
    {
        $quantityAsInt = (int) $quantity;
        if ($quantityAsInt < 2) {
            return null;
        }
        
        $total = $amount * intdiv($quantityAsInt, 2) + $quantityAsInt % 2 * $unitPrice;
        $discountN = $unitPrice * $quantity - $total;
        return new Discount($product, "2 for {$amount}", -$discountN);
    }

    private function calculateTenPercentDiscount(Product $product, float $quantity, float $unitPrice, float $percentage): Discount
    {
        return new Discount(
            $product,
            "{$percentage}% off",
            -$quantity * $unitPrice * $percentage / 100.0
        );
    }

    private function calculateFiveForAmountDiscount(Product $product, float $quantity, float $unitPrice, float $amount): ?Discount
    {
        $quantityAsInt = (int) $quantity;
        if ($quantityAsInt < 5) {
            return null;
        }
        
        $numberOfFives = intdiv($quantityAsInt, 5);
        $discountTotal = $unitPrice * $quantity - ($amount * $numberOfFives + $quantityAsInt % 5 * $unitPrice);
        return new Discount($product, "5 for {$amount}", -$discountTotal);
    }
}
