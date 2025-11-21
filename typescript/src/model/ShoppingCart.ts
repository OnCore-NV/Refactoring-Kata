import {Product} from "./Product"
import {SupermarketCatalog} from "./SupermarketCatalog"
import * as _ from "lodash"
import {ProductQuantity} from "./ProductQuantity"
import {Discount} from "./Discount"
import {Receipt} from "./Receipt"
import {Offer} from "./Offer"
import {SpecialOfferType} from "./SpecialOfferType"

// Constants for magic numbers
const DEFAULT_ITEM_QUANTITY = 1.0;
const TWO_FOR_AMOUNT_QUANTITY = 2;
const THREE_FOR_TWO_QUANTITY = 3;
const FIVE_FOR_AMOUNT_QUANTITY = 5;
const PERCENTAGE_DIVISOR = 100.0;

// Constants for discount description strings
const THREE_FOR_TWO_DESCRIPTION = "3 for 2";
const TWO_FOR_PREFIX = "2 for ";
const FOR_SEPARATOR = " for ";
const PERCENT_OFF_SUFFIX = "% off";

type ProductQuantities = { [productName: string]: ProductQuantity }
export type OffersByProduct = {[productName: string]: Offer};

export class ShoppingCart {

    private readonly  items: ProductQuantity[] = [];
    _productQuantities: ProductQuantities = {};


    getItems(): ProductQuantity[] {
        return _.clone(this.items);
    }

    addItem(product: Product): void {
        this.addItemQuantity(product, DEFAULT_ITEM_QUANTITY);
    }

    productQuantities(): ProductQuantities {
        return this._productQuantities;
    }


    public addItemQuantity(product: Product, quantity: number): void {
        let productQuantity = new ProductQuantity(product, quantity)
        this.items.push(productQuantity);
        let currentQuantity = this._productQuantities[product.name]
        if (currentQuantity) {
            this._productQuantities[product.name] = this.increaseQuantity(product, currentQuantity, quantity);
        } else {
            this._productQuantities[product.name] = productQuantity;
        }

    }

    private increaseQuantity(product: Product, productQuantity: ProductQuantity, quantity: number) {
        return new ProductQuantity(product, productQuantity.quantity + quantity)
    }

    handleOffers(receipt: Receipt,  offers: OffersByProduct, catalog: SupermarketCatalog ):void {
        for (const productName in this.productQuantities()) {
            const productQuantity = this._productQuantities[productName]
            const product = productQuantity.product;
            const quantity: number = this._productQuantities[productName].quantity;
            if (offers[productName]) {
                const offer : Offer = offers[productName];
                const unitPrice: number= catalog.getUnitPrice(product);
                let quantityAsInt = quantity;
                let discount : Discount|null = null;
                let x = 1;
                if (offer.offerType == SpecialOfferType.ThreeForTwo) {
                    x = THREE_FOR_TWO_QUANTITY;

                } else if (offer.offerType == SpecialOfferType.TwoForAmount) {
                    x = TWO_FOR_AMOUNT_QUANTITY;
                    if (quantityAsInt >= TWO_FOR_AMOUNT_QUANTITY) {
                        const total = offer.argument * Math.floor(quantityAsInt / x) + quantityAsInt % TWO_FOR_AMOUNT_QUANTITY * unitPrice;
                        const discountN = unitPrice * quantity - total;
                        discount = new Discount(product, TWO_FOR_PREFIX + offer.argument, discountN);
                    }

                } if (offer.offerType == SpecialOfferType.FiveForAmount) {
                    x = FIVE_FOR_AMOUNT_QUANTITY;
                }
                const numberOfXs = Math.floor(quantityAsInt / x);
                if (offer.offerType == SpecialOfferType.ThreeForTwo && quantityAsInt > TWO_FOR_AMOUNT_QUANTITY) {
                    const discountAmount = quantity * unitPrice - ((numberOfXs * TWO_FOR_AMOUNT_QUANTITY * unitPrice) + quantityAsInt % THREE_FOR_TWO_QUANTITY * unitPrice);
                    discount = new Discount(product, THREE_FOR_TWO_DESCRIPTION, discountAmount);
                }
                if (offer.offerType == SpecialOfferType.TenPercentDiscount) {
                    discount = new Discount(product, offer.argument + PERCENT_OFF_SUFFIX, quantity * unitPrice * offer.argument / PERCENTAGE_DIVISOR);
                }
                if (offer.offerType == SpecialOfferType.FiveForAmount && quantityAsInt >= FIVE_FOR_AMOUNT_QUANTITY) {
                    const discountTotal = unitPrice * quantity - (offer.argument * numberOfXs + quantityAsInt % FIVE_FOR_AMOUNT_QUANTITY * unitPrice);
                    discount = new Discount(product, x + FOR_SEPARATOR + offer.argument, discountTotal);
                }
                if (discount != null)
                    receipt.addDiscount(discount);
            }

        }
    }
}
