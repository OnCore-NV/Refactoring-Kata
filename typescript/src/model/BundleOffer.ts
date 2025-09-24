import {Bundle} from "./Bundle"
import {SpecialOfferType} from "./SpecialOfferType"

export class BundleOffer {
    public constructor(public readonly offerType: SpecialOfferType,
                       public readonly bundle: Bundle,
                       public readonly discountPercentage: number) {
    }

    getBundle(): Bundle {
        return this.bundle;
    }
}