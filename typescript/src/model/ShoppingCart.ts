import {Product} from "./Product"
import {SupermarketCatalog} from "./SupermarketCatalog"
import * as _ from "lodash"
import {ProductQuantity} from "./ProductQuantity"
import {Discount} from "./Discount"
import {Receipt} from "./Receipt"
import {Offer} from "./Offer"
import {SpecialOfferType} from "./SpecialOfferType"
import {BundleOffer} from "./BundleOffer"

type ProductQuantities = { [productName: string]: ProductQuantity }
export type OffersByProduct = {[productName: string]: Offer};

export class ShoppingCart {

    private readonly  items: ProductQuantity[] = [];
    _productQuantities: ProductQuantities = {};


    getItems(): ProductQuantity[] {
        return _.clone(this.items);
    }

    addItem(product: Product): void {
        this.addItemQuantity(product, 1.0);
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
                    x = 3;

                } else if (offer.offerType == SpecialOfferType.TwoForAmount) {
                    x = 2;
                    if (quantityAsInt >= 2) {
                        const total = offer.argument * Math.floor(quantityAsInt / x) + quantityAsInt % 2 * unitPrice;
                        const discountN = unitPrice * quantity - total;
                        discount = new Discount(product, "2 for " + offer.argument, discountN);
                    }

                } if (offer.offerType == SpecialOfferType.FiveForAmount) {
                    x = 5;
                }
                const numberOfXs = Math.floor(quantityAsInt / x);
                if (offer.offerType == SpecialOfferType.ThreeForTwo && quantityAsInt > 2) {
                    const discountAmount = quantity * unitPrice - ((numberOfXs * 2 * unitPrice) + quantityAsInt % 3 * unitPrice);
                    discount = new Discount(product, "3 for 2", discountAmount);
                }
                if (offer.offerType == SpecialOfferType.TenPercentDiscount) {
                    discount = new Discount(product, offer.argument + "% off", quantity * unitPrice * offer.argument / 100.0);
                }
                if (offer.offerType == SpecialOfferType.FiveForAmount && quantityAsInt >= 5) {
                    const discountTotal = unitPrice * quantity - (offer.argument * numberOfXs + quantityAsInt % 5 * unitPrice);
                    discount = new Discount(product, x + " for " + offer.argument, discountTotal);
                }
                if (discount != null)
                    receipt.addDiscount(discount);
            }

        }
    }

    handleBundleOffers(receipt: Receipt, bundleOffers: BundleOffer[], catalog: SupermarketCatalog): void {
        for (const bundleOffer of bundleOffers) {
            const bundle = bundleOffer.getBundle();
            const bundleProducts = bundle.getProducts();
            
            // Calculate how many complete bundles we can make
            let maxCompleteBundle = Number.MAX_SAFE_INTEGER;
            
            for (const bundleProduct of bundleProducts) {
                const productQuantity = this._productQuantities[bundleProduct.name];
                if (!productQuantity) {
                    maxCompleteBundle = 0;
                    break;
                }
                maxCompleteBundle = Math.min(maxCompleteBundle, Math.floor(productQuantity.quantity));
            }
            
            if (maxCompleteBundle > 0) {
                // Calculate discount for complete bundles
                let bundleTotal = 0;
                for (const bundleProduct of bundleProducts) {
                    const unitPrice = catalog.getUnitPrice(bundleProduct);
                    bundleTotal += unitPrice * maxCompleteBundle;
                }
                
                const discountAmount = bundleTotal * (bundleOffer.discountPercentage / 100.0);
                
                // Create a discount with the first product in the bundle as representative
                const discount = new Discount(
                    bundleProducts[0], 
                    `Bundle discount (${bundleOffer.discountPercentage}% off)`, 
                    discountAmount
                );
                receipt.addDiscount(discount);
            }
        }
    }
}
