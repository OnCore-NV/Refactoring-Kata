import {Product} from "./Product"

export class Bundle {
    constructor(public readonly products: Product[]) {
    }

    containsProduct(product: Product): boolean {
        return this.products.some(p => p.name === product.name);
    }

    getProducts(): Product[] {
        return [...this.products];
    }
}