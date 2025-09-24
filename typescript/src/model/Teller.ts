import {SupermarketCatalog} from "./SupermarketCatalog"
import {OffersByProduct, ShoppingCart} from "./ShoppingCart"
import {Product} from "./Product"
import {Receipt} from "./Receipt"
import {Offer} from "./Offer"
import {SpecialOfferType} from "./SpecialOfferType"
import {BundleOffer} from "./BundleOffer"
import {Bundle} from "./Bundle"

export class Teller {

    private offers: OffersByProduct = {};
    private bundleOffers: BundleOffer[] = [];

    public constructor(private readonly catalog: SupermarketCatalog ) {
    }

    public addSpecialOffer(offerType: SpecialOfferType , product: Product, argument: number): void {
        this.offers[product.name] = new Offer(offerType, product, argument);
    }

    public addBundleOffer(products: Product[], discountPercentage: number = 10): void {
        const bundle = new Bundle(products);
        const bundleOffer = new BundleOffer(SpecialOfferType.Bundle, bundle, discountPercentage);
        this.bundleOffers.push(bundleOffer);
    }

    public checksOutArticlesFrom(theCart: ShoppingCart): Receipt {
        const receipt = new Receipt();
        const productQuantities = theCart.getItems();
        for (let pq of productQuantities) {
            let p = pq.product;
            let quantity = pq.quantity;
            let unitPrice = this.catalog.getUnitPrice(p);
            let price = quantity * unitPrice;
            receipt.addProduct(p, quantity, unitPrice, price);
        }
        theCart.handleOffers(receipt, this.offers, this.catalog);
        theCart.handleBundleOffers(receipt, this.bundleOffers, this.catalog);

        return receipt;
    }

}
